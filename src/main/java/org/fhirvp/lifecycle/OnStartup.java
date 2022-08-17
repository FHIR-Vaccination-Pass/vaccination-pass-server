package org.fhirvp.lifecycle;

import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;
import org.fhirvp.usecase.BootstrapUseCase;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class OnStartup {
    @Inject
    LifecycleConfig lifecycleConfig;

    @Inject
    BootstrapUseCase bootstrapUseCase;

    void onStart(@Observes StartupEvent ev) throws FHIRServerException {
        if (lifecycleConfig.onStartup().bootstrap()) {
            Log.info("lifecycle.on-startup.bootstrap=true, running BootstrapUseCase");
            bootstrapUseCase.bootstrap();
        }
    }
}
