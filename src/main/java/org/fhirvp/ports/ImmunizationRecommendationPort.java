package org.fhirvp.ports;

import com.ibm.fhir.client.FHIRParameters;
import com.ibm.fhir.model.resource.ImmunizationRecommendation;
import org.fhirvp.model.mapper.ImmunizationRecommendationMapped;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;

import java.util.List;

public interface ImmunizationRecommendationPort extends FHIRResourcePort<ImmunizationRecommendation> {

    List<ImmunizationRecommendationMapped> searchReturnList(FHIRParameters searchParameters) throws FHIRServerException;

}
