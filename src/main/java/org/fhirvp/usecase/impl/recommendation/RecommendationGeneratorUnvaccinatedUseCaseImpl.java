package org.fhirvp.usecase.impl.recommendation;

import com.ibm.fhir.client.FHIRParameters;
import com.ibm.fhir.model.resource.Basic;
import com.ibm.fhir.model.resource.ImmunizationRecommendation;
import io.quarkus.logging.Log;
import org.fhirvp.Constants;
import org.fhirvp.model.ForecastStatus;
import org.fhirvp.model.MedicationWithPersonalizedRecommendations;
import org.fhirvp.model.PersonalizedRecommendation;
import org.fhirvp.model.VaccinationSchemeType;
import org.fhirvp.model.builder.FHIRResourceBuilder;
import org.fhirvp.model.mapper.*;
import org.fhirvp.ports.ActiveVaccinationSchemePort;
import org.fhirvp.ports.ImmunizationRecommendationPort;
import org.fhirvp.ports.VaccinationDosePort;
import org.fhirvp.ports.VaccinationSchemePort;
import org.fhirvp.usecase.RecommendationGenerationUtils;
import org.fhirvp.usecase.RecommendationGeneratorUnvaccinatedUseCase;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class RecommendationGeneratorUnvaccinatedUseCaseImpl implements RecommendationGeneratorUnvaccinatedUseCase {

    @Inject
    RecommendationGenerationUtils recommendationGenerationUtils;

    @Inject
    FHIRResourceBuilder fhirResourceBuilder;

    @Inject
    VaccinationSchemePort vaccinationSchemePort;

    @Inject
    VaccinationDosePort vaccinationDosePort;

    @Inject
    ImmunizationRecommendationPort immunizationRecommendationPort;

    @Inject
    ActiveVaccinationSchemePort activeVaccinationSchemePort;

    @Override
    public void generate(PatientMapped patient, List<MedicationMapped> medicationsSorted, List<PersonalizedRecommendation> unvaccinatedPersonalizedRecommendations) {
        List<MedicationWithPersonalizedRecommendations> medicationWithPersonalizedRecommendationsList = selectMedicationsForUnvaccinatedPersonalRecommendations(medicationsSorted, unvaccinatedPersonalizedRecommendations);

        medicationWithPersonalizedRecommendationsList
                .forEach(medicationWithPersonalizedRecommendations -> {
                    VaccinationSchemeMapped vaccinationScheme = selectVaccinationScheme(patient, medicationWithPersonalizedRecommendations);

                    FHIRParameters vaccinationDoseParameters = new FHIRParameters()
                            .searchParam("subject:Basic", vaccinationScheme.getId())
                            .searchParam("vaccinationDoseNumberInScheme", "1");
                    VaccinationDoseMapped vaccinationDose = vaccinationDosePort.trySearchReturnList(vaccinationDoseParameters).get()
                            .get(0);

                    medicationWithPersonalizedRecommendations.getPersonalizedRecommendations()
                            .forEach(personalizedRecommendation -> {
                                ImmunizationRecommendation immunizationRecommendation = fhirResourceBuilder.buildImmunizationRecommendation(
                                        patient.getId(),
                                        medicationWithPersonalizedRecommendations.getMedication().getCode(),
                                        personalizedRecommendation.getTargetDisease().getCode(),
                                        ForecastStatus.DUE,
                                        Optional.of(LocalDate.now()),
                                        Optional.of(LocalDate.now()),
                                        Optional.of(LocalDate.now()),
                                        List.of("dummy-id"), // TODO: Remove dummy id as soon as frontend supports that this is not present
                                        false,
                                        Optional.empty(),
                                        personalizedRecommendation.getPopulationRecommendation().getId(),
                                        vaccinationDose.getId());
                                immunizationRecommendationPort.tryCreate(immunizationRecommendation).get();
                                Log.info("Recommendation for " + medicationWithPersonalizedRecommendations.getMedication().getTradeName() + " created (Unvaccinated).");
                            });

                    Basic activeVaccinationScheme = fhirResourceBuilder.buildActiveVaccinationScheme(
                            patient.getId(),
                            vaccinationScheme.getId(),
                            "Initial automatic generation from Vaccination Pass Server"
                    );
                    activeVaccinationSchemePort.tryCreate(activeVaccinationScheme);
                });
    }

    private List<MedicationWithPersonalizedRecommendations> selectMedicationsForUnvaccinatedPersonalRecommendations(List<MedicationMapped> medicationsSorted, List<PersonalizedRecommendation> personalizedRecommendations) {
        List<PersonalizedRecommendation> unmatchedPersonalRecommendations = personalizedRecommendations;
        List<MedicationWithPersonalizedRecommendations> medicationsToGive = new LinkedList<>();
        for (MedicationMapped medication : medicationsSorted) {
            boolean giveMedication = unmatchedPersonalRecommendations.stream()
                    .map(unmatchedPersonalizedRecommendation -> unmatchedPersonalizedRecommendation.getTargetDisease().getCode())
                    .collect(Collectors.toList())
                    .containsAll(medication.getTargetDiseaseCodes());
            if (giveMedication) {
                List<PersonalizedRecommendation> personalizedRecommendationsOfMedication = unmatchedPersonalRecommendations.stream()
                        .filter(personalizedRecommendation -> medication.getTargetDiseaseCodes().contains(personalizedRecommendation.getTargetDisease().getCode()))
                        .collect(Collectors.toList());
                unmatchedPersonalRecommendations.removeAll(personalizedRecommendationsOfMedication);
                medicationsToGive.add(new MedicationWithPersonalizedRecommendations(medication, personalizedRecommendationsOfMedication));
                if (unmatchedPersonalRecommendations.isEmpty()) {
                    break;
                }
            }
        }
        return medicationsToGive;
    }

    private VaccinationSchemeMapped selectVaccinationScheme(PatientMapped patient, MedicationWithPersonalizedRecommendations medicationWithPersonalizedRecommendations) {
        FHIRParameters vaccinationSchemeParameters = new FHIRParameters()
                .searchParam("subject:Medication", medicationWithPersonalizedRecommendations.getMedication().getId());

        // Get all VaccinationSchemes that could be used on this patient in this situation
        List<VaccinationSchemeMapped> applicableVaccinationSchemes = vaccinationSchemePort.trySearchReturnList(vaccinationSchemeParameters).get().stream()
                // Remove all VaccinationSchemes of not applicable ages
                .filter(vaccinationScheme -> recommendationGenerationUtils.isAgeApplicable(patient, vaccinationScheme))
                // This class is for unvaccinated recommendations so we have to start with a STANDARD or FAST scheme and not a BOOSTER scheme
                .filter(vaccinationScheme -> vaccinationScheme.getType() == VaccinationSchemeType.STANDARD || vaccinationScheme.getType() == VaccinationSchemeType.FAST)
                .collect(Collectors.toList());

        // For travel vaccinations: Remove all VaccinationSchemes that are too slow
        List<VaccinationSchemeMapped> inTimePossibleVaccinationSchemes = applicableVaccinationSchemes.stream()
                .filter(vaccinationScheme -> isVaccinationSchemeFastEnough(vaccinationScheme, medicationWithPersonalizedRecommendations.getPersonalizedRecommendations()))
                .collect(Collectors.toList());

        // Use a STANDARD scheme if possible
        boolean containsStandardScheme = inTimePossibleVaccinationSchemes.stream()
                .anyMatch(vaccinationScheme -> vaccinationScheme.getType() == VaccinationSchemeType.STANDARD);
        if (containsStandardScheme) {
            inTimePossibleVaccinationSchemes = inTimePossibleVaccinationSchemes.stream()
                    .filter(vaccinationScheme -> vaccinationScheme.getType() == VaccinationSchemeType.STANDARD)
                    .collect(Collectors.toList());
        }

        // Use a preferred scheme if possible
        boolean containsIsPreferredScheme = inTimePossibleVaccinationSchemes.stream()
                .anyMatch(VaccinationSchemeMapped::isPreferred);
        if (containsIsPreferredScheme) {
            inTimePossibleVaccinationSchemes = inTimePossibleVaccinationSchemes.stream()
                    .filter(VaccinationSchemeMapped::isPreferred)
                    .collect(Collectors.toList());
        }

        return inTimePossibleVaccinationSchemes.stream()
                .findFirst()
                // If no scheme is possible in time, return the fastest one possible for this patient
                .orElse(getFastestVaccinationScheme(applicableVaccinationSchemes));
    }

    private boolean isVaccinationSchemeFastEnough(VaccinationSchemeMapped vaccinationScheme, List<PersonalizedRecommendation> personalizedRecommendations) {
        Optional<LocalDate> earliestDepartureDate = personalizedRecommendations.stream()
                // Get all departure dates
                .flatMap(personalizedRecommendation -> {
                    return personalizedRecommendation.getVacationPlans().stream()
                            .map(VacationPlanMapped::getDepartureDate);
                })
                // We onyl want the earliest departure date
                .min(LocalDate::compareTo);
        if (earliestDepartureDate.isEmpty()) {
            return true;
        }
        long daysToProtection = getDaysToProtection(vaccinationScheme) + Constants.DAYS_VACATION_IMMUNIZATION_DONE_BEFORE_DEPARTURE;
        return LocalDate.now().plusDays(daysToProtection).isBefore(earliestDepartureDate.get());
    }

    private VaccinationSchemeMapped getFastestVaccinationScheme(List<VaccinationSchemeMapped> vaccinationSchemes) {
        return vaccinationSchemes.stream()
                .min((vs1, vs2) -> (int) (getDaysToProtection(vs1) - getDaysToProtection(vs2)))
                .get();

    }

    private long getDaysToProtection(VaccinationSchemeMapped vaccinationScheme) {
        FHIRParameters vaccinationDoseParameters = new FHIRParameters()
                .searchParam("subject:Basic", vaccinationScheme.getId());
        List<VaccinationDoseMapped> vaccinationDosesSorted = vaccinationDosePort.trySearchReturnList(vaccinationDoseParameters).get().stream()
                .sorted(Comparator.comparingInt(vd -> vd.getVaccinationDoseSingleExtension().get().getNumberInScheme()))
                .collect(Collectors.toList());
        long daysToProtection = 0;
        for (VaccinationDoseMapped vaccinationDose : vaccinationDosesSorted) {
            VaccinationDoseSingleMapped vaccinationDoseSingle = vaccinationDose.getVaccinationDoseSingleExtension().get();
            if (vaccinationDoseSingle.getNumberInScheme() != 1) {
                daysToProtection += vaccinationDoseSingle.getTimeframeStartInDays().get();
            }
            if (vaccinationDose.isProtected()) {
                break;
            }
        }
        return daysToProtection;
    }

}
