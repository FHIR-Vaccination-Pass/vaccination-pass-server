package org.fhirvp.ports.impl.fhir;

import com.ibm.fhir.model.resource.Organization;
import org.fhirvp.ports.OrganizationPort;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class OrganizationPortImpl extends FHIRResourcePortImpl<Organization> implements OrganizationPort {
    @Inject
    OrganizationPortImpl(FHIRClientProvider fhirClientProvider) {
        resourceClass = Organization.class;
        resourceName = "Organization";
        resourcePath = "/organization";

        postFailMsg = "POST " + resourcePath + " failed";
        getFailMsg = "GET " + resourcePath + " failed";
        putFailMsg = "PUT " + resourcePath + " failed";
        deleteFailmsg = "DELETE " + resourcePath + " failed";
        noLocationmsg = resourceName + " has no location";
        castFailMsg = resourceName + " is not a " + resourceName;

        fhirClient = fhirClientProvider.getFhirClient();
    }
}
