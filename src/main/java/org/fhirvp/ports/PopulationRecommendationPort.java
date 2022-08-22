package org.fhirvp.ports;

import com.ibm.fhir.client.FHIRParameters;
import com.ibm.fhir.model.resource.Basic;
import org.fhirvp.model.mapper.PopulationRecommendationMapped;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;

import java.util.List;

public interface PopulationRecommendationPort extends FHIRResourcePort<Basic> {

    List<PopulationRecommendationMapped> searchReturnList(FHIRParameters searchParameters) throws FHIRServerException;

}
