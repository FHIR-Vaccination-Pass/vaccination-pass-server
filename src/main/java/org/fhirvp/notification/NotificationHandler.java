package org.fhirvp.notification;

public interface NotificationHandler {

    void handleNotification(String notificationJson);

    void handleNotification(FHIRNotificationEvent notification);

}
