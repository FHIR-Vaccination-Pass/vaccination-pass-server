package org.fhirvp.ports.impl.fhir;

import com.ibm.fhir.client.FHIRClient;
import com.ibm.fhir.client.FHIRParameters;
import com.ibm.fhir.model.resource.Bundle;
import com.ibm.fhir.model.resource.Resource;
import io.vavr.control.Try;
import org.fhirvp.Constants;
import org.fhirvp.ports.FHIRResourcePort;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;

import javax.inject.Inject;

import static org.fhirvp.ports.impl.fhir.FHIRClientUtils.*;
import static org.fhirvp.ports.impl.fhir.FHIRClientUtils.wrapDelete;

public abstract class FHIRResourcePortImpl<T extends Resource> implements FHIRResourcePort<T> {
    Class<T> resourceClass;
    String resourceName;
    String resourcePath;

    @Inject
    FHIRClient fhirClient;

    @Override
    public String create(T resource) throws FHIRServerException {
        var response = wrapCreate(() -> fhirClient.create(resource), getPostFailMsg());
        return rethrow(response::getLocation, getNoLocationMsg());
    }

    @Override
    public Try<String> tryCreate(T resource) {
        return Try.of(() -> create(resource));
    }

    @Override
    public T read(String id) throws FHIRServerException {
        var response = wrapRead(() -> fhirClient.read(resourceName, id), getGetFailMsg());
        // .getResource(Class<T> type) forces us to implement the abstract base class pattern here
        // because T.class is invalid due to Java's type erasure:
        // https://docs.oracle.com/javase/tutorial/java/generics/erasure.html
        return rethrow(() -> response.getResource(resourceClass), getCastFailMsg());
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
    public Bundle search(FHIRParameters parameters) throws FHIRServerException {
        final FHIRParameters parametersFinal = parameters == null ? new FHIRParameters() : parameters;
        parametersFinal.count(Constants.SEARCH_BUNDLE_COUNT);
        var response = wrapRead(() -> fhirClient.search(resourceName, parametersFinal), getGetFailMsg());
        return rethrow(() -> response.getResource(Bundle.class), "FHIR Search didn't return a bundle.");
    }

    @Override
    public Try<Bundle> trySearch(FHIRParameters parameters) {
        return Try.of(() -> search(parameters));
    }

    @Override
    public void update(T resource) throws FHIRServerException {
        wrapCreate(() -> fhirClient.update(resource), getPutFailMsg());
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
        wrapDelete(() -> fhirClient.delete(resourceName, id), getDeleteFailMsg());
    }

    @Override
    public Try<Void> tryDelete(String id) {
        return Try.of(() -> {
            delete(id);
            return null;
        });
    }

    private String getPostFailMsg() {
        return "POST " + resourcePath + " failed";
    }

    public String getGetFailMsg() {
        return "GET " + resourcePath + " failed";
    }

    public String getPutFailMsg() {
        return "PUT " + resourcePath + " failed";
    }

    public String getDeleteFailMsg() {
        return "DELETE " + resourcePath + " failed";
    }

    public String getNoLocationMsg() {
        return resourceName + " has no location";
    }

    public String getCastFailMsg() {
        return resourceName + " is not a " + resourceName;
    }
}
