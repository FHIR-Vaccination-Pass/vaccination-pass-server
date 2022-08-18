package org.fhirvp.usecase;

import org.fhirvp.model.FHIRNotificationEvent;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;

public interface OnPatientNotificationUseCase {

    void handle(FHIRNotificationEvent notification) throws FHIRServerException;

}
