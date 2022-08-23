package org.fhirvp.ports;

import com.ibm.fhir.client.FHIRParameters;
import com.ibm.fhir.model.resource.Basic;
import org.fhirvp.model.mapper.ActiveVaccinationSchemeMapped;
import org.fhirvp.model.mapper.VacationPlanMapped;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;

import java.util.List;

public interface ActiveVaccinationSchemePort extends FHIRResourcePort<Basic> {

    List<ActiveVaccinationSchemeMapped> searchReturnList(FHIRParameters searchParameters) throws FHIRServerException;

}
