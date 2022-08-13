package org.fhirvp.notification.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.arc.Unremovable;
import org.fhirvp.notification.FHIRNotificationEvent;
import org.fhirvp.notification.NotificationHandler;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Unremovable
public class NotificationHandlerImpl implements NotificationHandler {

    @Override
    public void handleNotification(String notificationJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            handleNotification(objectMapper.readValue(notificationJson, FHIRNotificationEvent.class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleNotification(FHIRNotificationEvent notification) {
        System.out.println("handleNotification(FHIRNotificationEvent notification) has been reached: " + notification);
        // TODO
    }

}
