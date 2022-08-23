package org.fhirvp.ports;

import com.ibm.fhir.client.FHIRParameters;
import com.ibm.fhir.model.resource.Immunization;
import org.fhirvp.model.mapper.ImmunizationMapped;
import org.fhirvp.model.mapper.VacationPlanMapped;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;

import java.util.List;

public interface ImmunizationPort extends FHIRResourcePort<Immunization> {

    List<ImmunizationMapped> searchReturnList(FHIRParameters searchParameters) throws FHIRServerException;

}
