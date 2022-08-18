package org.fhirvp.ports.impl.fhir;

import com.ibm.fhir.model.resource.Organization;
import org.fhirvp.ports.OrganizationPort;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrganizationPortImpl extends FHIRResourcePortImpl<Organization> implements OrganizationPort {
    OrganizationPortImpl() {
        resourceClass = Organization.class;
        resourceName = "Organization";
        resourcePath = "/organization";
    }
}
