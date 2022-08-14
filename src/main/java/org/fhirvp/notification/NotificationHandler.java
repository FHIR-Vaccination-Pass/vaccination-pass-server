package org.fhirvp.notification;

public interface NotificationHandler {

    void handleNotification(FHIRNotificationEvent notification);

}
