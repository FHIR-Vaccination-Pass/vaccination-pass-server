package org.fhirvp.ports.impl.fhir;

import com.ibm.fhir.model.resource.ImmunizationRecommendation;
import org.fhirvp.ports.ImmunizationRecommendationPort;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ImmunizationRecommendationPortImpl extends FHIRResourcePortImpl<ImmunizationRecommendation> implements ImmunizationRecommendationPort {
    @Inject
    ImmunizationRecommendationPortImpl(FHIRClientProvider fhirClientProvider) {
        resourceClass = ImmunizationRecommendation.class;
        resourceName = "ImmunizationRecommendation";
        resourcePath = "/immunizationrecommendation";

        postFailMsg = "POST " + resourcePath + " failed";
        getFailMsg = "GET " + resourcePath + " failed";
        putFailMsg = "PUT " + resourcePath + " failed";
        deleteFailmsg = "DELETE " + resourcePath + " failed";
        noLocationmsg = resourceName + " has no location";
        castFailMsg = resourceName + " is not a " + resourceName;

        fhirClient = fhirClientProvider.getFhirClient();
    }
}
