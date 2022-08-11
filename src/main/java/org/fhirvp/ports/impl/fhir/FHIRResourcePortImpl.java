package org.fhirvp.ports.impl.fhir;

import com.ibm.fhir.client.FHIRClient;
import com.ibm.fhir.model.resource.Resource;
import io.vavr.control.Try;
import org.fhirvp.ports.FHIRResourcePort;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;

import static org.fhirvp.ports.impl.fhir.FHIRClientUtils.*;
import static org.fhirvp.ports.impl.fhir.FHIRClientUtils.wrapDelete;

public abstract class FHIRResourcePortImpl<T extends Resource> implements FHIRResourcePort<T> {
    Class<T> resourceClass;
    String resourceName;
    String resourcePath;
    String postFailMsg;
    String getFailMsg;
    String putFailMsg;
    String deleteFailmsg;
    String noLocationmsg;
    String castFailMsg;

    FHIRClient fhirClient;

    @Override
    public String create(T resource) throws FHIRServerException {
        var response = wrapCreate(() -> fhirClient.create(resource), postFailMsg);
        return rethrow(response::getLocation, noLocationmsg);
    }

    @Override
    public Try<String> tryCreate(T resource) {
        return Try.of(() -> create(resource));
    }

    @Override
    public T read(String id) throws FHIRServerException {
        var response = wrapRead(() -> fhirClient.read(resourceName, id), getFailMsg);
        // .getResource(Class<T> type) forces us to implement the abstract base class pattern here
        // because T.class is invalid due to Java's type erasure:
        // https://docs.oracle.com/javase/tutorial/java/generics/erasure.html
        return rethrow(() -> response.getResource(resourceClass), castFailMsg);
    }

    @Override
    public Try<T> tryRead(String id) {
        return Try.of(() -> read(id));
    }

    @Override
    public T createAndRead(T resource) throws FHIRServerException {
        var resourceId = create(resource);
        return read(resourceId);
    }

    @Override
    public Try<T> tryCreateAndRead(T resource) {
        return Try.of(() -> createAndRead(resource));
    }

    @Override
    public void update(T resource) throws FHIRServerException {
        var response = wrapCreate(() -> fhirClient.update(resource), putFailMsg);
    }

    @Override
    public Try<Void> tryUpdate(T resource) {
        return Try.of(() -> {
            update(resource);
            return null;
        });
    }

    @Override
    public void delete(String id) throws FHIRServerException {
        var response = wrapDelete(() -> fhirClient.delete(resourceName, id), deleteFailmsg);
    }

    @Override
    public Try<Void> tryDelete(String id) {
        return Try.of(() -> {
            delete(id);
            return null;
        });
    }
}
