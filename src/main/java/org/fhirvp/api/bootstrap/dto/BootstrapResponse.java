package org.fhirvp.api.bootstrap.dto;

import com.ibm.fhir.model.resource.Patient;
import lombok.Value;
import org.fhirvp.usecase.BootstrapUseCase;

import java.util.List;

@Value
public class BootstrapResponse {
    List<Patient> patients;

    public static BootstrapResponse from(BootstrapUseCase.BootstrapResult bootstrapResult) {
        return new BootstrapResponse(bootstrapResult.getPatients());
    }
}
