package org.fhirvp.usecase;

import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;

public interface RecommendationGeneratorForPatientUseCase {

    void generate(String patientId) throws FHIRServerException;

}
