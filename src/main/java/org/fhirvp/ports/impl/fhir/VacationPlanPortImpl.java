package org.fhirvp.ports.impl.fhir;

import com.ibm.fhir.client.FHIRParameters;
import com.ibm.fhir.model.resource.Basic;
import com.ibm.fhir.model.resource.Bundle;
import io.vavr.control.Try;
import org.fhirvp.Constants;
import org.fhirvp.ports.VacationPlanPort;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VacationPlanPortImpl extends FHIRResourcePortImpl<Basic> implements VacationPlanPort {

    VacationPlanPortImpl() {
        resourceClass = Basic.class;
        resourceName = "Basic";
        resourcePath = "/basic";
    }

    @Override
    public Bundle search(FHIRParameters parameters) throws FHIRServerException {
        if (parameters == null) {
            parameters = new FHIRParameters();
        }
        parameters.searchParam("code", "VacationPlan");
        parameters.searchParam("_profile", Constants.PROFILE_BASE_URL + "vp-vacation-plan");
        return super.search(parameters);
    }

    @Override
    public Try<Bundle> trySearch(FHIRParameters parameters) {
        return Try.of(() -> this.search(parameters));
    }

}
