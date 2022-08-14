package org.fhirvp.websocket;

import io.quarkus.runtime.StartupEvent;
import org.fhirvp.websocket.context.FHIRNotificationConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import java.io.IOException;
import java.net.URI;

@ApplicationScoped
public class FHIRNotificationClient {

    Session fhirNotificationClientSession;

    @Inject
    FHIRNotificationConfig config;

    public void onStart(@Observes StartupEvent event) throws DeploymentException, IOException {
        fhirNotificationClientSession = ContainerProvider.getWebSocketContainer().connectToServer(FHIRNotificationClientEndpoint.class, URI.create(config.websocket().url()));
    }

}
