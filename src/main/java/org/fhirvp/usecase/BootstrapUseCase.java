package org.fhirvp.usecase;

import com.ibm.fhir.model.resource.*;
import lombok.Builder;
import lombok.Value;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;

import java.util.List;

public interface BootstrapUseCase {
    @Builder
    @Value
    class BootstrapResult {
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
    }

    BootstrapResult bootstrap() throws FHIRServerException;
}
