package org.fhirvp.ports.impl.fhir;

import com.ibm.fhir.model.resource.Patient;
import org.fhirvp.ports.PatientPort;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PatientPortImpl extends FHIRResourcePortImpl<Patient> implements PatientPort {
    @Inject
    PatientPortImpl(FHIRClientProvider fhirClientProvider) {
        resourceClass = Patient.class;
        resourceName = "Patient";
        resourcePath = "/patient";

        postFailMsg = "POST " + resourcePath + " failed";
        getFailMsg = "GET " + resourcePath + " failed";
        putFailMsg = "PUT " + resourcePath + " failed";
        deleteFailmsg = "DELETE " + resourcePath + " failed";
        noLocationmsg = resourceName + " has no location";
        castFailMsg = resourceName + " is not a " + resourceName;

        fhirClient = fhirClientProvider.getFhirClient();
    }
}
