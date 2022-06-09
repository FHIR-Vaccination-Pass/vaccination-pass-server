package org.fhirvp.ports.impl.fhir;

import com.ibm.fhir.client.FHIRClient;
import com.ibm.fhir.model.resource.Patient;
import org.fhirvp.ports.PatientPort;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.fhirvp.ports.impl.fhir.FHIRClientUtils.*;

@ApplicationScoped
public class PatientPortImpl implements PatientPort {
    FHIRClient fhirClient;

    @Inject
    PatientPortImpl(FHIRClientProvider fhirClientProvider) {
        fhirClient = fhirClientProvider.getFhirClient();
    }

    @Override
    public String create(Patient patient) throws FHIRServerException {
        var response = wrapCreate(
                () -> fhirClient.create(patient),
                "POST /patient failed"
        );
        return rethrow(
                response::getLocation,
                "Patient has no location"
        );
    }

    @Override
    public Patient read(String id) throws FHIRServerException {
        var response = wrapRead(
                () -> fhirClient.read("Patient", id),
                "GET /patient failed"
        );
        return rethrow(
                () -> response.getResource(Patient.class),
                "Patient is not a Patient"
        );
    }

    @Override
    public void update(Patient patient) throws FHIRServerException {
        var response = wrapCreate(
                () ->fhirClient.update(patient),
                "PUT /patient failed"
        );
    }

    @Override
    public void delete(String id) throws FHIRServerException {
        var response = wrapDelete(
                () -> fhirClient.delete("Patient", id),
                "DELETE /patient failed"
        );
    }
}
