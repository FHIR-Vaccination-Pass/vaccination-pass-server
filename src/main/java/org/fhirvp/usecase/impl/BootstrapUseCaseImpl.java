package org.fhirvp.usecase.impl;

import com.ibm.fhir.model.resource.Patient;
import com.ibm.fhir.model.type.HumanName;
import org.fhirvp.ports.PatientPort;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;
import org.fhirvp.usecase.BootstrapUseCase;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class BootstrapUseCaseImpl implements BootstrapUseCase {
    @Inject
    PatientPort patientPort;

    @Override
    public BootstrapResult bootstrap() throws FHIRServerException {
        var patient = Patient.builder()
                .name(HumanName.builder()
                        .given("John")
                        .family("Doe")
                        .build())
                .build();

        var patientId = patientPort.create(patient);
        patient = patientPort.read(patientId);

        return new BootstrapResult(List.of(patient));
    }
}
