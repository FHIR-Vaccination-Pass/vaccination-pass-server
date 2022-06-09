package org.fhirvp.ports;

import com.ibm.fhir.model.resource.Patient;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;

public interface PatientPort {
    String create(Patient patient) throws FHIRServerException;
    Patient read(String id) throws FHIRServerException;
    void update(Patient patient) throws FHIRServerException;
    void delete(String id) throws FHIRServerException;
}
