package org.fhirvp.api.bootstrap;

import com.ibm.fhir.model.resource.Patient;
import com.ibm.fhir.model.type.HumanName;
import org.apache.http.HttpStatus;
import org.fhirvp.api.bootstrap.dto.BootstrapResponse;
import org.fhirvp.repository.fhir.FHIRRepository;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/bootstrap")
public class BootstrapResource {
    @Inject
    FHIRRepository fhirRepository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public BootstrapResponse bootstrap() throws Exception {
        var fhirClient = fhirRepository.getFhirClient();

        var patient = Patient.builder()
                .name(HumanName.builder()
                        .given("John")
                        .family("Doe")
                        .build())
                .build();
        var patientCreateResponse = fhirClient.create(patient);
        if (patientCreateResponse.getStatus() != HttpStatus.SC_CREATED) {
            throw new InternalServerErrorException(patientCreateResponse.getResponse().readEntity(String.class));
        }
        var patientLocation = patientCreateResponse.getLocation();

        var serverPatientResponse = fhirClient.read("Patient", patientLocation);
        if (serverPatientResponse.getStatus() != HttpStatus.SC_OK) {
            throw new InternalServerErrorException(serverPatientResponse.getResponse().readEntity(String.class));
        }
        var serverPatient = serverPatientResponse.getResource(Patient.class);

        return new BootstrapResponse(patientLocation, serverPatient);
    }
}
