package org.fhirvp.api.bootstrap.dto;

import com.ibm.fhir.model.resource.*;
import lombok.Builder;
import lombok.Value;
import org.fhirvp.usecase.BootstrapUseCase;

import java.util.List;

@Builder
@Value
public class BootstrapResponse {
    List<Basic> activeVaccinationSchemes;
    List<Immunization> immunizations;
    List<ImmunizationRecommendation> immunizationRecommendations;
    List<Medication> medications;
    List<Organization> organizations;
    List<Patient> patients;
    List<Basic> populationRecommendations;
    List<Practitioner> practitioners;
    List<Basic> targetDiseases;
    List<Basic> vacationPlans;
    List<Basic> vaccinationDoses;
    List<Basic> vaccinationSchemes;

    public static BootstrapResponse from(BootstrapUseCase.BootstrapResult bootstrapResult) {
        return BootstrapResponse.builder()
                .activeVaccinationSchemes(bootstrapResult.getActiveVaccinationSchemes())
                .immunizations(bootstrapResult.getImmunizations())
                .immunizationRecommendations(bootstrapResult.getImmunizationRecommendations())
                .medications(bootstrapResult.getMedications())
                .organizations(bootstrapResult.getOrganizations())
                .patients(bootstrapResult.getPatients())
                .populationRecommendations(bootstrapResult.getPopulationRecommendations())
                .practitioners(bootstrapResult.getPractitioners())
                .targetDiseases(bootstrapResult.getTargetDiseases())
                .vacationPlans(bootstrapResult.getVacationPlans())
                .vaccinationDoses(bootstrapResult.getVaccinationDoses())
                .vaccinationSchemes(bootstrapResult.getVaccinationSchemes())
                .build();
    }
}
