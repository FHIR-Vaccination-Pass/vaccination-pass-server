package org.fhirvp.ports.impl.fhir;

import com.ibm.fhir.model.resource.Practitioner;
import org.fhirvp.ports.PractitionerPort;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PractitionerPortImpl extends FHIRResourcePortImpl<Practitioner> implements PractitionerPort {
    @Inject
    PractitionerPortImpl(FHIRClientProvider fhirClientProvider) {
        resourceClass = Practitioner.class;
        resourceName = "Practitioner";
        resourcePath = "/practitioner";

        postFailMsg = "POST " + resourcePath + " failed";
        getFailMsg = "GET " + resourcePath + " failed";
        putFailMsg = "PUT " + resourcePath + " failed";
        deleteFailmsg = "DELETE " + resourcePath + " failed";
        noLocationmsg = resourceName + " has no location";
        castFailMsg = resourceName + " is not a " + resourceName;

        fhirClient = fhirClientProvider.getFhirClient();
    }
}
