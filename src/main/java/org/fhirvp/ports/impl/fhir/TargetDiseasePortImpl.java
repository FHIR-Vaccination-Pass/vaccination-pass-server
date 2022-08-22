package org.fhirvp.ports.impl.fhir;

import com.ibm.fhir.client.FHIRParameters;
import com.ibm.fhir.model.resource.Basic;
import com.ibm.fhir.model.resource.Bundle;
import io.vavr.control.Try;
import org.fhirvp.Constants;
import org.fhirvp.model.mapper.TargetDiseaseMapped;
import org.fhirvp.model.mapper.VacationPlanMapped;
import org.fhirvp.ports.TargetDiseasePort;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class TargetDiseasePortImpl extends FHIRResourcePortImpl<Basic> implements TargetDiseasePort {

    TargetDiseasePortImpl() {
        resourceClass = Basic.class;
        resourceName = "Basic";
        resourcePath = "/basic";
    }

    @Override
    public Bundle search(FHIRParameters searchParameters) throws FHIRServerException {
        FHIRParameters parameters = searchParameters == null ? new FHIRParameters() : searchParameters;
        parameters.searchParam("code", "TargetDisease");
        parameters.searchParam("_profile", Constants.PROFILE_BASE_URL + "vp-target-disease");
        return super.search(parameters);
    }

    @Override
    public Try<Bundle> trySearch(FHIRParameters parameters) {
        return Try.of(() -> this.search(parameters));
    }

    public List<TargetDiseaseMapped> searchReturnList(FHIRParameters searchParameters) throws FHIRServerException {
        return this.search(searchParameters).getEntry()
                .stream().map(entry -> new TargetDiseaseMapped(entry.getResource().as(Basic.class)))
                .collect(Collectors.toList());
    }

    public Try<List<TargetDiseaseMapped>> trySearchReturnList(FHIRParameters searchParameters) {
        return Try.of(() -> this.searchReturnList(searchParameters));
    }

}
