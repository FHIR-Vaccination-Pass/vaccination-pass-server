package org.fhirvp.lifecycle;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "lifecycle")
public interface LifecycleConfig {
    interface OnStartup {
        boolean bootstrap();
    }
    OnStartup onStartup();
}
