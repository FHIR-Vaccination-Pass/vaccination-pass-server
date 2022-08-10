package org.fhirvp.ports;

import com.ibm.fhir.model.resource.Resource;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;

public interface FHIRResourcePort<T extends Resource> {
    String create(T resource) throws FHIRServerException;
    T read(String id) throws FHIRServerException;
    void update(T resource) throws FHIRServerException;
    void delete(String id) throws FHIRServerException;
}
