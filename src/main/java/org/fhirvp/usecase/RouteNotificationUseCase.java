package org.fhirvp.usecase;

import org.fhirvp.model.FHIRNotificationEvent;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;

public interface RouteNotificationUseCase {

    void route(FHIRNotificationEvent notification) throws FHIRServerException;

}
