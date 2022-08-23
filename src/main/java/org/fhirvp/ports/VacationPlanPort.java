package org.fhirvp.ports;

import com.ibm.fhir.client.FHIRParameters;
import com.ibm.fhir.model.resource.Basic;
import org.fhirvp.model.mapper.VacationPlanMapped;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;

import java.util.List;

public interface VacationPlanPort extends FHIRResourcePort<Basic> {

    List<VacationPlanMapped> searchReturnList(FHIRParameters searchParameters) throws FHIRServerException;

}
