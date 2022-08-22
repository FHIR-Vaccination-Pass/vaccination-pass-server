package org.fhirvp.ports;

import com.ibm.fhir.client.FHIRParameters;
import com.ibm.fhir.model.resource.Medication;
import org.fhirvp.model.mapper.MedicationMapped;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;

import java.util.List;

public interface MedicationPort extends FHIRResourcePort<Medication> {

    List<MedicationMapped> searchReturnList(FHIRParameters searchParameters) throws FHIRServerException;

}
