package org.fhirvp.ports.impl.fhir;

import com.ibm.fhir.model.resource.Practitioner;
import org.fhirvp.ports.PractitionerPort;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PractitionerPortImpl extends FHIRResourcePortImpl<Practitioner> implements PractitionerPort {
    PractitionerPortImpl() {
        resourceClass = Practitioner.class;
        resourceName = "Practitioner";
        resourcePath = "/practitioner";
    }
}
