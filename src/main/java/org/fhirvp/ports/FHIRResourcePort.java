package org.fhirvp.ports;

import com.ibm.fhir.model.resource.Resource;
import io.vavr.control.Try;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;

public interface FHIRResourcePort<T extends Resource> {
    String create(T resource) throws FHIRServerException;
    Try<String> tryCreate(T resource);
    T read(String id) throws FHIRServerException;
    Try<T> tryRead(String id);
    T createAndRead(T resource) throws FHIRServerException;
    Try<T> tryCreateAndRead(T resource);
    void update(T resource) throws FHIRServerException;
    Try<Void> tryUpdate(T resource);
    void delete(String id) throws FHIRServerException;
    Try<Void> tryDelete(String id);
}
