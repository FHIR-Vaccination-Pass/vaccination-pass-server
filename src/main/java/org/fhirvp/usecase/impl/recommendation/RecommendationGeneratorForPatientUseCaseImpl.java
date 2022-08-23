package org.fhirvp.usecase.impl.recommendation;

import com.ibm.fhir.client.FHIRParameters;
import com.ibm.fhir.model.resource.Basic;
import org.fhirvp.model.AdministeredMedication;
import org.fhirvp.model.PersonalizedRecommendation;
import org.fhirvp.model.mapper.*;
import org.fhirvp.ports.*;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;
import org.fhirvp.usecase.RecommendationGenerationUtils;
import org.fhirvp.usecase.RecommendationGeneratorForPatientUseCase;
import org.fhirvp.usecase.RecommendationGeneratorUnvaccinatedUseCase;
import org.fhirvp.usecase.RecommendationGeneratorVaccinatedUseCase;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class RecommendationGeneratorForPatientUseCaseImpl implements RecommendationGeneratorForPatientUseCase {

    @Inject
    RecommendationGenerationUtils recommendationGenerationUtils;

    @Inject
    RecommendationGeneratorUnvaccinatedUseCase recommendationGeneratorUnvaccinatedUseCase;

    @Inject
    RecommendationGeneratorVaccinatedUseCase recommendationGeneratorVaccinatedUseCase;

    @Inject
    PatientPort patientPort;

    @Inject
    VacationPlanPort vacationPlanPort;

    @Inject
    PopulationRecommendationPort populationRecommendationPort;

    @Inject
    TargetDiseasePort targetDiseasePort;

    @Inject
    ImmunizationPort immunizationPort;

    @Inject
    MedicationPort medicationPort;

    @Inject
    ImmunizationRecommendationPort immunizationRecommendationPort;

    public void generate(String patientId) throws FHIRServerException {
        PatientMapped patient = new PatientMapped(patientPort.read(patientId));

        List<PersonalizedRecommendation> personalizedRecommendations = generatePersonalizedRecommendations(patient);

        FHIRParameters immunizationParameters = new FHIRParameters()
                .searchParam("patient", patient.getId());
        List<ImmunizationMapped> immunizations = immunizationPort.searchReturnList(immunizationParameters);

        FHIRParameters medicationWithImmunizationParameters = new FHIRParameters()
                .searchParam("code", immunizations.stream()
                        .map(immunization -> "http://fhir.de/CodeSystem/ifa/pzn|" + immunization.getVaccineCode())
                        .collect(Collectors.joining(",")));
        List<MedicationMapped> medicationsWithImmunizations = medicationPort.searchReturnList(medicationWithImmunizationParameters);

        List<AdministeredMedication> administeredMedications = medicationsWithImmunizations.stream()
                .map(medication -> {
                    List<ImmunizationMapped> immunizationsOfMedication = immunizations.stream()
                            .filter(immunization -> immunization.getVaccineCode().equals(medication.getCode()))
                            .collect(Collectors.toList());
                    List<PersonalizedRecommendation> personalizedRecommendationsOfMedication = personalizedRecommendations.stream()
                            .filter(personalizedRecommendation -> {
                                return medication.getTargetDiseaseCodes().stream()
                                        .anyMatch(medicationTargetDiseaseCode -> medicationTargetDiseaseCode.equals(personalizedRecommendation.getTargetDisease().getCode()));
                            })
                            .collect(Collectors.toList());
                    return new AdministeredMedication(medication, immunizationsOfMedication, personalizedRecommendationsOfMedication);
                })
                .collect(Collectors.toList());

        // Remove all personalizedRecommendations that have an administeredMedication
        personalizedRecommendations.removeAll(
                administeredMedications.stream()
                        .flatMap(administeredMedication -> administeredMedication.getPersonalizedRecommendations().stream())
                        .distinct()
                        .collect(Collectors.toList())
        );

        FHIRParameters medicationWithoutImmunizationParameters = new FHIRParameters()
                .searchParam("code:missing", immunizations.stream()
                        .map(immunization -> "http://fhir.de/CodeSystem/ifa/pzn|" + immunization.getVaccineCode())
                        .collect(Collectors.joining(",")));
        List<MedicationMapped> medicationsWithoutImmunizationsSorted = medicationPort.searchReturnList(medicationWithoutImmunizationParameters).stream()
                .sorted((m1, m2) -> m2.getTargetDiseaseCodes().size() - m1.getTargetDiseaseCodes().size())
                .collect(Collectors.toList());

        deleteAllImmunizationRecommendationsOfPatient(patient);

        // Generate recommendations for personalized recommendations without prior immunizations
        recommendationGeneratorUnvaccinatedUseCase.generate(patient, medicationsWithoutImmunizationsSorted, personalizedRecommendations);

        // Generate recommendations for next doses of already administered vaccines
        recommendationGeneratorVaccinatedUseCase.generate(patient, administeredMedications);
    }

    private List<PersonalizedRecommendation> generatePersonalizedRecommendations(PatientMapped patient) throws FHIRServerException {
        FHIRParameters vacationPlanParameters = new FHIRParameters()
                .searchParam("subject:Patient", patient.getId());
        List<VacationPlanMapped> vacationPlans = vacationPlanPort.searchReturnList(vacationPlanParameters);

        return populationRecommendationPort.search(null).getEntry().stream()
                .map(entry -> new PopulationRecommendationMapped(entry.getResource().as(Basic.class)))
                // Remove all PopulationRecommendations of not applicable ages
                .filter(populationRecommendation -> recommendationGenerationUtils.isAgeApplicable(patient, populationRecommendation))
                // Build PersonalizedRecommendations for applicable locations
                .map(populationRecommendation -> buildPersonalizedRecommendation(populationRecommendation, patient, vacationPlans))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Optional<PersonalizedRecommendation> buildPersonalizedRecommendation(PopulationRecommendationMapped populationRecommendation, PatientMapped patient, List<VacationPlanMapped> vacationPlans) {
        if (populationRecommendation.getLocationsMapped().isEmpty()) {
            return Optional.of(new PersonalizedRecommendation(populationRecommendation, getTargetDiseaseOfPopulationRecommendation(populationRecommendation), List.of()));
        }
        boolean patientAddressHit = populationRecommendation.getLocationsMapped().stream()
                .anyMatch(recommendationLocation -> {
                    return patient.getAddressMapped().stream()
                            .anyMatch(address -> {
                                if (recommendationLocation.getStateCode().isPresent()) {
                                    // TODO: Change back to equals as soon as frontend returns the correct code (i.e. DE-BY instead of BY)
                                    //return recommendationLocation.getStateCode().get().equals(address.getStateCode());
                                    return address.getStateCode().contains(recommendationLocation.getStateCode().get());
                                } else {
                                    return recommendationLocation.getCountryCode().equals(address.getCountryCode());
                                }
                            });
                });
        if (patientAddressHit) {
            return Optional.of(new PersonalizedRecommendation(populationRecommendation, getTargetDiseaseOfPopulationRecommendation(populationRecommendation), List.of()));
        }
        List<VacationPlanMapped> vacationPlanHits = populationRecommendation.getLocationsMapped().stream()
                .flatMap(populationRecommendationLocation -> getVacationPlansForRecommendationLocation(populationRecommendationLocation, vacationPlans))
                .collect(Collectors.toList());
        if (vacationPlanHits.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(new PersonalizedRecommendation(populationRecommendation, getTargetDiseaseOfPopulationRecommendation(populationRecommendation), vacationPlans));
        }
    }

    private TargetDiseaseMapped getTargetDiseaseOfPopulationRecommendation(PopulationRecommendationMapped populationRecommendation) {
        FHIRParameters targetDiseaseParameters = new FHIRParameters()
                .searchParam("targetDiseaseCode", "http://hl7.org/fhir/sid/icd-10|" + populationRecommendation.getTargetDiseaseCode());
        List<TargetDiseaseMapped> targetDiseases = targetDiseasePort.trySearchReturnList(targetDiseaseParameters).get();
        assert targetDiseases.size() == 1;
        return targetDiseases.get(0);
    }

    // Get all vacationPlans that go to a same location as the populationRecommendationLocation
    private Stream<VacationPlanMapped> getVacationPlansForRecommendationLocation(LocationMapped populationRecommendationLocation, List<VacationPlanMapped> vacationPlans) {
        return vacationPlans.stream()
                .filter(vacationPlan -> {
                    return vacationPlan.getLocationsMapped().stream()
                            .anyMatch(vacationPlanLocation -> {
                                if (vacationPlanLocation.getStateCode().isPresent()) {
                                    if (populationRecommendationLocation.getStateCode().isPresent()) {
                                        return populationRecommendationLocation.getStateCode().get().equals(vacationPlanLocation.getStateCode().get());
                                    } else {
                                        return populationRecommendationLocation.getCountryCode().equals(vacationPlanLocation.getCountryCode());
                                    }
                                } else {
                                    return populationRecommendationLocation.getCountryCode().equals(vacationPlanLocation.getCountryCode());
                                }
                            });
                });
    }

    private void deleteAllImmunizationRecommendationsOfPatient(PatientMapped patient) throws FHIRServerException {
        FHIRParameters immunizationRecommendationParameters = new FHIRParameters()
                .searchParam("patient", patient.getId());
        immunizationRecommendationPort.searchReturnList(immunizationRecommendationParameters)
                .forEach(immunizationRecommendation -> immunizationRecommendationPort.tryDelete(immunizationRecommendation.getId()).get());
    }

}
