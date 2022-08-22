package org.fhirvp.ports.impl.fhir;

import com.ibm.fhir.client.FHIRParameters;
import com.ibm.fhir.model.resource.Basic;
import com.ibm.fhir.model.resource.ImmunizationRecommendation;
import org.fhirvp.model.mapper.ImmunizationRecommendationMapped;
import org.fhirvp.model.mapper.VacationPlanMapped;
import org.fhirvp.ports.ImmunizationRecommendationPort;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ImmunizationRecommendationPortImpl extends FHIRResourcePortImpl<ImmunizationRecommendation> implements ImmunizationRecommendationPort {
    ImmunizationRecommendationPortImpl() {
        resourceClass = ImmunizationRecommendation.class;
        resourceName = "ImmunizationRecommendation";
        resourcePath = "/immunizationrecommendation";
    }

    public List<ImmunizationRecommendationMapped> searchReturnList(FHIRParameters searchParameters) throws FHIRServerException {
        return this.search(searchParameters).getEntry()
                .stream().map(entry -> new ImmunizationRecommendationMapped(entry.getResource().as(ImmunizationRecommendation.class)))
                .collect(Collectors.toList());
    }

}
