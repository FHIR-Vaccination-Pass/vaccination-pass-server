package org.fhirvp.ports;

import com.ibm.fhir.client.FHIRParameters;
import com.ibm.fhir.model.resource.Patient;
import org.fhirvp.model.mapper.PatientMapped;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;

import java.util.List;

public interface PatientPort extends FHIRResourcePort<Patient> {

    List<PatientMapped> searchReturnList(FHIRParameters searchParameters) throws FHIRServerException;

}
