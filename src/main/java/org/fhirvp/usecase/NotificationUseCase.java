package org.fhirvp.usecase;

import org.fhirvp.model.FHIRNotificationEvent;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;

public interface NotificationUseCase {

    void handleNotification(FHIRNotificationEvent notification) throws FHIRServerException;

}
