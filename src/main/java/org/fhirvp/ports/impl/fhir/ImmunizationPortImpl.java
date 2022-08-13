package org.fhirvp.ports.impl.fhir;

import com.ibm.fhir.model.resource.Immunization;
import org.fhirvp.ports.ImmunizationPort;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ImmunizationPortImpl extends FHIRResourcePortImpl<Immunization> implements ImmunizationPort {
    ImmunizationPortImpl() {
        resourceClass = Immunization.class;
        resourceName = "Immunization";
        resourcePath = "/immunization";

        postFailMsg = "POST " + resourcePath + " failed";
        getFailMsg = "GET " + resourcePath + " failed";
        putFailMsg = "PUT " + resourcePath + " failed";
        deleteFailmsg = "DELETE " + resourcePath + " failed";
        noLocationmsg = resourceName + " has no location";
        castFailMsg = resourceName + " is not a " + resourceName;
    }
}
