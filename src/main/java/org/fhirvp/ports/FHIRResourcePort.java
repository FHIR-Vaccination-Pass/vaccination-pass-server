package org.fhirvp.ports;

import com.ibm.fhir.client.FHIRParameters;
import com.ibm.fhir.model.resource.Bundle;
import com.ibm.fhir.model.resource.Resource;
import io.vavr.control.Try;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;

public interface FHIRResourcePort<T extends Resource> extends FHIRResourcePortMinimal<T> {

    String create(T resource) throws FHIRServerException;
    Try<String> tryCreate(T resource);

    T createAndRead(T resource) throws FHIRServerException;
    Try<T> tryCreateAndRead(T resource);

    Bundle search(FHIRParameters parameters) throws FHIRServerException;
    Try<Bundle> trySearch(FHIRParameters parameters);

    void update(T resource) throws FHIRServerException;
    Try<Void> tryUpdate(T resource);

    void delete(String id) throws FHIRServerException;
    Try<Void> tryDelete(String id);

}
