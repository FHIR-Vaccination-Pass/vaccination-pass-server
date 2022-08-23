package org.fhirvp.usecase.impl.recommendation;

import com.ibm.fhir.client.FHIRParameters;
import com.ibm.fhir.model.resource.Basic;
import com.ibm.fhir.model.resource.ImmunizationRecommendation;
import io.quarkus.logging.Log;
import org.fhirvp.model.AdministeredMedication;
import org.fhirvp.model.ForecastStatus;
import org.fhirvp.model.VaccinationSchemeType;
import org.fhirvp.model.builder.FHIRResourceBuilder;
import org.fhirvp.model.mapper.*;
import org.fhirvp.ports.ActiveVaccinationSchemePort;
import org.fhirvp.ports.ImmunizationRecommendationPort;
import org.fhirvp.ports.VaccinationDosePort;
import org.fhirvp.ports.VaccinationSchemePort;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;
import org.fhirvp.usecase.RecommendationGenerationUtils;
import org.fhirvp.usecase.RecommendationGeneratorVaccinatedUseCase;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class RecommendationGeneratorVaccinatedUseCaseImpl implements RecommendationGeneratorVaccinatedUseCase {

    @Inject
    RecommendationGenerationUtils recommendationGenerationUtils;

    @Inject
    FHIRResourceBuilder fhirResourceBuilder;

    @Inject
    ActiveVaccinationSchemePort activeVaccinationSchemePort;

    @Inject
    VaccinationSchemePort vaccinationSchemePort;

    @Inject
    VaccinationDosePort vaccinationDosePort;

    @Inject
    ImmunizationRecommendationPort immunizationRecommendationPort;

    public void generate(PatientMapped patient, List<AdministeredMedication> administeredMedications) throws FHIRServerException {
        FHIRParameters activeVaccinationSchemeParameters = new FHIRParameters()
                .searchParam("subject:Patient", patient.getId());
        List<ActiveVaccinationSchemeMapped> activeVaccinationSchemes = activeVaccinationSchemePort.searchReturnList(activeVaccinationSchemeParameters);
        Map<String, ActiveVaccinationSchemeMapped> vaccinationSchemeIdToActiveVaccinationScheme = new HashMap<>();
        activeVaccinationSchemes.forEach(activeVaccinationScheme -> vaccinationSchemeIdToActiveVaccinationScheme.put(activeVaccinationScheme.getVaccinationSchemeId(), activeVaccinationScheme));

        for (AdministeredMedication administeredMedication : administeredMedications) {
            FHIRParameters vaccinationSchemeParameters = new FHIRParameters()
                    .searchParam("_id", activeVaccinationSchemes.stream()
                            .map(ActiveVaccinationSchemeMapped::getVaccinationSchemeId)
                            .collect(Collectors.joining(",")))
                    .searchParam("subject:Medication", administeredMedication.getMedication().getId());
            List<VaccinationSchemeMapped> vaccinationSchemeList = vaccinationSchemePort.searchReturnList(vaccinationSchemeParameters);
            if (vaccinationSchemeList.isEmpty()) {
                // This Medication doesn't have an ActiveVaccinationScheme. We can ignore it.
                continue;
            } else if (vaccinationSchemeList.size() >= 2) {
                Log.error("Medication has more than one ActiveVaccinationScheme.");
            }

            // Now, we know that this Medication has an ActiveVaccinationScheme
            VaccinationSchemeMapped vaccinationScheme = vaccinationSchemeList.get(0);

            // TODO: Change medication (and therefore vaccination scheme) to something that exactly fulfills the target diseases in administeredMedication.getPersonalizedRecommendations()
            if (administeredMedication.getMedication().getTargetDiseaseCodes().size() != administeredMedication.getPersonalizedRecommendations().size()) {
                Log.error("Change of medication due to target disease change is not implemented.");
                continue;
            }
            // TODO: Change vaccination scheme (and possibly medication) if patient isn't in age range anymore
            if (!recommendationGenerationUtils.isAgeApplicable(patient, vaccinationScheme)) {
                Log.error("Change of vaccination scheme (and possibly medication) due to patient being outside of age range is not implemented.");
                continue;
            }

            // Check if the last dose was a repeating dose and recommend another shot of it
            List<ImmunizationMapped> immunizationsSorted = administeredMedication.getImmunizations().stream()
                    .sorted((o1, o2) -> o1.getOccurrence().compareTo(o2.getOccurrence()))
                    .collect(Collectors.toList());
            ImmunizationMapped lastImmunization = immunizationsSorted.get(immunizationsSorted.size() - 1);
            VaccinationDoseMapped lastVaccinationDose = new VaccinationDoseMapped(vaccinationDosePort.read(lastImmunization.getAdministeredVaccinationDoseId()));
            if (lastVaccinationDose.getVaccinationDoseRepeatingMapped().isPresent()) {
                // Give another shot of the repeating dose in current scheme
                generateImmunizationRecommendation(patient, administeredMedication, lastImmunization, lastVaccinationDose);
                continue;
            } else if (lastVaccinationDose.getVaccinationDoseSingleExtension().isEmpty()) {
                Log.error("VaccinationDose doesn't have a SingleDose or RepeatingDose.");
            }

            // Check if there is a next single dose in the current scheme and recommend it
            FHIRParameters nextSingleDoseInSchemeParameters = new FHIRParameters()
                    .searchParam("subject:Basic", vaccinationScheme.getId())
                    .searchParam("vaccinationDoseNumberInScheme", Integer.toString(lastVaccinationDose.getVaccinationDoseSingleExtension().get().getNumberInScheme() + 1));
            VaccinationDoseMapped nextSingleDoseInScheme = vaccinationDosePort.searchReturnList(nextSingleDoseInSchemeParameters).stream()
                    .findFirst()
                    .orElse(null);
            if (nextSingleDoseInScheme != null) {
                // Give next single dose in current scheme
                generateImmunizationRecommendation(patient, administeredMedication, lastImmunization, nextSingleDoseInScheme);
                continue;
            }

            // Check if there is a repeating dose in current scheme and recommend it
            FHIRParameters repeatingDoseInSchemeParameters = new FHIRParameters()
                    .searchParam("subject:Basic", vaccinationScheme.getId())
                    .searchParam("vaccinationDoseIsRepeating", "true");
            VaccinationDoseMapped repeatingDoseInScheme = vaccinationDosePort.searchReturnList(repeatingDoseInSchemeParameters).stream()
                    .findFirst()
                    .orElse(null);
            if (repeatingDoseInScheme != null) {
                // Give the first repeating dose in current scheme
                generateImmunizationRecommendation(patient, administeredMedication, lastImmunization, repeatingDoseInScheme);
                continue;
            }

            // Now we know that all doses of the current scheme are done -> Change scheme if possible
            if (vaccinationScheme.getType() == VaccinationSchemeType.BOOSTER) {
                // We already are in a BOOSTER scheme and done with it. Nothing can follow. Patient is fully immunized.
                continue;
            } else if (vaccinationScheme.getType() != VaccinationSchemeType.STANDARD && vaccinationScheme.getType() != VaccinationSchemeType.FAST) {
                Log.error("VaccinationScheme has an unsupported type: " + vaccinationScheme.getType());
            }

            // Select best possible BOOSTER scheme as the next scheme
            FHIRParameters boosterSchemeParameters = new FHIRParameters()
                    .searchParam("subject:Medication", administeredMedication.getMedication().getId())
                    .searchParam("vaccinationSchemeType", "booster");
            List<VaccinationSchemeMapped> boosterSchemes = vaccinationSchemePort.searchReturnList(boosterSchemeParameters);
            if (boosterSchemes.isEmpty()) {
                // No BOOSTER scheme exists. Nothing can follow. Patient is fully immunized.
                continue;
            }
            boolean containsPreferredScheme = boosterSchemes.stream()
                    .anyMatch(VaccinationSchemeMapped::isPreferred);
            VaccinationSchemeMapped nextScheme = null;
            if (containsPreferredScheme) {
                nextScheme = boosterSchemes.stream()
                        .filter(VaccinationSchemeMapped::isPreferred)
                        .findFirst()
                        .orElseThrow();
            } else {
                nextScheme = boosterSchemes.get(0);
            }

            // Check if next scheme has a single dose and recommend it
            FHIRParameters singleDoseNextSchemeParameters = new FHIRParameters()
                    .searchParam("subject:Basic", nextScheme.getId())
                    .searchParam("vaccinationDoseNumberInScheme", "1");
            VaccinationDoseMapped singleDoseNextScheme = vaccinationDosePort.searchReturnList(singleDoseNextSchemeParameters).stream()
                    .findFirst()
                    .orElse(null);
            if (singleDoseNextScheme != null) {
                // Give the first single dose of the next scheme
                generateImmunizationRecommendation(patient, administeredMedication, lastImmunization, singleDoseNextScheme);
                changeActiveVaccinationScheme(vaccinationSchemeIdToActiveVaccinationScheme.get(vaccinationScheme.getId()), nextScheme, "Automatic change to booster scheme by Vaccination Pass Server");
                continue;
            }

            // NextScheme doesn't have a single dose. There has to be a repeating dose that we can recommend. Otherwise, the scheme would be completely empty.
            FHIRParameters repeatingDoseNextSchemeParameters = new FHIRParameters()
                    .searchParam("subject:Basic", nextScheme.getId())
                    .searchParam("vaccinationDoseIsRepeating", "true");
            VaccinationDoseMapped repeatingDoseNextScheme = vaccinationDosePort.searchReturnList(repeatingDoseNextSchemeParameters).stream()
                    .findFirst()
                    .orElse(null);
            if (repeatingDoseNextScheme == null) {
                Log.error("VaccinationScheme without any doses found. Id: " + nextScheme.getId());
                continue;
            }
            generateImmunizationRecommendation(patient, administeredMedication, lastImmunization, repeatingDoseNextScheme);
            changeActiveVaccinationScheme(vaccinationSchemeIdToActiveVaccinationScheme.get(vaccinationScheme.getId()), nextScheme, "Automatic change to booster scheme by Vaccination Pass Server");
        }
    }

    private void generateImmunizationRecommendation(PatientMapped patient, AdministeredMedication administeredMedication, ImmunizationMapped previousImmunization, VaccinationDoseMapped doseToGive) {
        Optional<LocalDate> dateToGive = Optional.empty();
        Optional<LocalDate> earliestDateToGive;
        Optional<LocalDate> latestDateToGive = Optional.empty();
        if (doseToGive.getVaccinationDoseSingleExtension().isPresent()) {
            VaccinationDoseSingleMapped vaccinationDoseSingle = doseToGive.getVaccinationDoseSingleExtension().get();
            // Since this can't be the first dose of this medication, both timeframes have to be present
            earliestDateToGive = Optional.of(previousImmunization.getOccurrence().plusDays(vaccinationDoseSingle.getTimeframeStartInDays().get()));
            latestDateToGive = Optional.of(previousImmunization.getOccurrence().plusDays(vaccinationDoseSingle.getTimeframeEndInDays().get()));
        } else {
            // This has to be a repeating dose
            dateToGive = Optional.of(previousImmunization.getOccurrence().plusDays(doseToGive.getVaccinationDoseRepeatingMapped().get().getIntervalInDays()));
            earliestDateToGive = Optional.of(dateToGive.get());
        }

        Optional<LocalDate> finalDateToGive = dateToGive;
        Optional<LocalDate> finalEarliestDateToGive = earliestDateToGive;
        Optional<LocalDate> finalLatestDateToGive = latestDateToGive;
        administeredMedication.getPersonalizedRecommendations()
                .forEach(personalizedRecommendation -> {
                    ImmunizationRecommendation immunizationRecommendation = fhirResourceBuilder.buildImmunizationRecommendation(
                            patient.getId(),
                            administeredMedication.getMedication().getCode(),
                            personalizedRecommendation.getTargetDisease().getCode(),
                            ForecastStatus.DUE,
                            finalDateToGive,
                            finalEarliestDateToGive,
                            finalLatestDateToGive,
                            administeredMedication.getImmunizations().stream().map(ImmunizationMapped::getId).collect(Collectors.toList()),
                            false,
                            Optional.empty(),
                            personalizedRecommendation.getPopulationRecommendation().getId(),
                            doseToGive.getId());
                    immunizationRecommendationPort.tryCreate(immunizationRecommendation).get();
                    Log.info("Recommendation for " + administeredMedication.getMedication().getTradeName() + " created (Vaccinated).");
                });
    }

    private void changeActiveVaccinationScheme(ActiveVaccinationSchemeMapped oldActiveVaccinationScheme, VaccinationSchemeMapped newVaccinationScheme, String reason) throws FHIRServerException {
        activeVaccinationSchemePort.delete(oldActiveVaccinationScheme.getId());
        Basic activeVaccinationScheme = fhirResourceBuilder.buildActiveVaccinationScheme(
                oldActiveVaccinationScheme.getPatientId(),
                newVaccinationScheme.getId(),
                reason);
        activeVaccinationSchemePort.create(activeVaccinationScheme);
    }

}
