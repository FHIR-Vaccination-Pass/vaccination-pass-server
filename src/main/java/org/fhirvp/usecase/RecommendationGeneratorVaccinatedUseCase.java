package org.fhirvp.usecase;

import org.fhirvp.model.AdministeredMedication;
import org.fhirvp.model.mapper.PatientMapped;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;

import java.util.List;

public interface RecommendationGeneratorVaccinatedUseCase {

    void generate(PatientMapped patient, List<AdministeredMedication> administeredMedications) throws FHIRServerException;

}
