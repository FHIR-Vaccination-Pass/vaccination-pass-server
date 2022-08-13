package org.fhirvp.websocket;

import io.quarkus.logging.Log;
import org.fhirvp.notification.NotificationHandler;

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
    void handleIncomingFHIRNotification(String msg) {
        Log.info("Received FHIR Notification: " + msg);

        // Workaround to get NotificationHandler since @Inject doesn't work with @ClientEndpoint
        CDI.current().select(NotificationHandler.class).get().handleNotification(msg);
    }
}
