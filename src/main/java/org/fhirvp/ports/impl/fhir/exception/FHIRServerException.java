package org.fhirvp.ports.impl.fhir.exception;

import com.ibm.fhir.client.FHIRResponse;

public class FHIRServerException extends Exception {
    FHIRResponse response;

    public FHIRServerException(String errorMessage, FHIRResponse response) {
        super(errorMessage);
        this.response = response;
    }

    public FHIRServerException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public FHIRServerException(String errorMessage, FHIRResponse response, Throwable err) {
        super(errorMessage, err);
        this.response = response;
    }
}
