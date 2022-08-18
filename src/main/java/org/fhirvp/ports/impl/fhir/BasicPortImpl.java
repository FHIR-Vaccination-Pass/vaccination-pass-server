package org.fhirvp.ports.impl.fhir;

import com.ibm.fhir.model.resource.Basic;
import org.fhirvp.ports.BasicPort;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BasicPortImpl extends FHIRResourcePortImpl<Basic> implements BasicPort {
    BasicPortImpl() {
        resourceClass = Basic.class;
        resourceName = "Basic";
        resourcePath = "/basic";
    }
}
