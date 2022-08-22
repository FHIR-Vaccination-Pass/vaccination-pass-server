package org.fhirvp.ports;

import com.ibm.fhir.client.FHIRParameters;
import com.ibm.fhir.model.resource.Basic;
import io.vavr.control.Try;
import org.fhirvp.model.mapper.TargetDiseaseMapped;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;

import java.util.List;

public interface TargetDiseasePort extends FHIRResourcePort<Basic> {

    List<TargetDiseaseMapped> searchReturnList(FHIRParameters searchParameters) throws FHIRServerException;

    Try<List<TargetDiseaseMapped>> trySearchReturnList(FHIRParameters searchParameters);

}
