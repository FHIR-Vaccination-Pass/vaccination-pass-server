package org.fhirvp.ports.impl.fhir;

import com.ibm.fhir.client.FHIRParameters;
import com.ibm.fhir.model.resource.Basic;
import com.ibm.fhir.model.resource.Bundle;
import io.vavr.control.Try;
import org.fhirvp.Constants;
import org.fhirvp.ports.VaccinationDosePort;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VaccinationDosePortImpl extends FHIRResourcePortImpl<Basic> implements VaccinationDosePort {

    VaccinationDosePortImpl() {
        resourceClass = Basic.class;
        resourceName = "Basic";
        resourcePath = "/basic";
    }

    @Override
    public Bundle search(FHIRParameters parameters) throws FHIRServerException {
        if (parameters == null) {
            parameters = new FHIRParameters();
        }
        parameters.searchParam("code", "VaccinationDose");
        parameters.searchParam("_profile", Constants.PROFILE_BASE_URL + "vp-vaccination-dose");
        return super.search(parameters);
    }

    @Override
    public Try<Bundle> trySearch(FHIRParameters parameters) {
        return Try.of(() -> this.search(parameters));
    }

}
