package org.fhirvp.usecase;

import com.ibm.fhir.model.resource.Patient;
import lombok.Value;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;

import java.util.List;

public interface BootstrapUseCase {
    @Value
    class BootstrapResult {
        List<Patient> patients;
    }

    BootstrapResult bootstrap() throws FHIRServerException;
}
