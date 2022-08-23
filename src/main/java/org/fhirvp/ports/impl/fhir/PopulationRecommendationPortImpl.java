package org.fhirvp.ports.impl.fhir;

import com.ibm.fhir.client.FHIRParameters;
import com.ibm.fhir.model.resource.Basic;
import com.ibm.fhir.model.resource.Bundle;
import io.vavr.control.Try;
import org.fhirvp.Constants;
import org.fhirvp.model.mapper.PopulationRecommendationMapped;
import org.fhirvp.model.mapper.VacationPlanMapped;
import org.fhirvp.ports.PopulationRecommendationPort;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class PopulationRecommendationPortImpl extends FHIRResourcePortImpl<Basic> implements PopulationRecommendationPort {

    PopulationRecommendationPortImpl() {
        resourceClass = Basic.class;
        resourceName = "Basic";
        resourcePath = "/basic";
    }

    @Override
    public Bundle search(FHIRParameters searchParameters) throws FHIRServerException {
        FHIRParameters parameters = searchParameters == null ? new FHIRParameters() : searchParameters;
        parameters.searchParam("code", "PopulationRecommendation");
        parameters.searchParam("_profile", Constants.PROFILE_BASE_URL + "vp-population-recommendation");
        return super.search(parameters);
    }

    @Override
    public Try<Bundle> trySearch(FHIRParameters parameters) {
        return Try.of(() -> this.search(parameters));
    }

    public List<PopulationRecommendationMapped> searchReturnList(FHIRParameters searchParameters) throws FHIRServerException {
        return this.search(searchParameters).getEntry()
                .stream().map(entry -> new PopulationRecommendationMapped(entry.getResource().as(Basic.class)))
                .collect(Collectors.toList());
    }

}
