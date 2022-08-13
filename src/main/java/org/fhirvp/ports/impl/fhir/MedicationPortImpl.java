package org.fhirvp.ports.impl.fhir;

import com.ibm.fhir.model.resource.Medication;
import org.fhirvp.ports.MedicationPort;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MedicationPortImpl extends FHIRResourcePortImpl<Medication> implements MedicationPort {
    MedicationPortImpl() {
        resourceClass = Medication.class;
        resourceName = "Medication";
        resourcePath = "/medication";

        postFailMsg = "POST " + resourcePath + " failed";
        getFailMsg = "GET " + resourcePath + " failed";
        putFailMsg = "PUT " + resourcePath + " failed";
        deleteFailmsg = "DELETE " + resourcePath + " failed";
        noLocationmsg = resourceName + " has no location";
        castFailMsg = resourceName + " is not a " + resourceName;
    }
}
