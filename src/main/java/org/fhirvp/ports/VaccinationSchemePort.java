package org.fhirvp.ports;

import com.ibm.fhir.client.FHIRParameters;
import com.ibm.fhir.model.resource.Basic;
import io.vavr.control.Try;
import org.fhirvp.model.mapper.VaccinationSchemeMapped;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;

import java.util.List;

public interface VaccinationSchemePort extends FHIRResourcePort<Basic> {

    List<VaccinationSchemeMapped> searchReturnList(FHIRParameters searchParameters) throws FHIRServerException;

    Try<List<VaccinationSchemeMapped>> trySearchReturnList(FHIRParameters searchParameters);

}
