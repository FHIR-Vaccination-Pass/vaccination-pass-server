package org.fhirvp.websocket.context;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "fhir-notification")
public interface FHIRNotificationConfig {
    interface Websocket {
        String url();
    }
    Websocket websocket();
}
