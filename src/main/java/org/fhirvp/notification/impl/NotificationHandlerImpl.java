package org.fhirvp.notification.impl;

import io.quarkus.arc.Unremovable;
import org.fhirvp.notification.FHIRNotificationEvent;
import org.fhirvp.notification.NotificationHandler;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Unremovable // This bean is used in FHIRNotificationClientEndpoint without @Inject so Quarkus would remove it. @Unremovable prevents this.
public class NotificationHandlerImpl implements NotificationHandler {

    @Override
    public void handleNotification(FHIRNotificationEvent notification) {
        System.out.println("handleNotification(FHIRNotificationEvent notification) has been reached: " + notification);
        // TODO
    }

}
