package org.fhirvp.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.logging.Log;
import org.fhirvp.model.FHIRNotificationEvent;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;
import org.fhirvp.usecase.RouteNotificationUseCase;

import javax.enterprise.inject.spi.CDI;
import javax.websocket.ClientEndpoint;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

@ClientEndpoint
public class FHIRNotificationClientEndpoint {

    @OnOpen
    void onOpen(Session session) {
        Log.info("WebSocket session with URI '" + session.getRequestURI() + "' opened.");
    }

    @OnMessage
    void handleIncomingFHIRNotification(String notificationJson) throws JsonProcessingException, FHIRServerException {
        ObjectMapper objectMapper = new ObjectMapper();
        FHIRNotificationEvent notification = objectMapper.readValue(notificationJson, FHIRNotificationEvent.class);
        Log.info("Received FHIR Notification: " + notification);

        // Workaround to get NotificationUseCase since @Inject doesn't work with @ClientEndpoint
        RouteNotificationUseCase routeNotificationUseCase = CDI.current().select(RouteNotificationUseCase.class).get();
        // TODO: Activate as soon as backend should react to notifications
        //routeNotificationUseCase.route(notification);
    }
}
