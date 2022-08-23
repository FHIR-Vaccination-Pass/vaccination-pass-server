package org.fhirvp.usecase.impl.notification;

import io.quarkus.logging.Log;
import org.fhirvp.model.FHIRNotificationEvent;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;
import org.fhirvp.usecase.OnPatientNotificationUseCase;
import org.fhirvp.usecase.RecommendationGeneratorForPatientUseCase;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class OnPatientNotificationUseCaseImpl implements OnPatientNotificationUseCase {

    @Inject
    RecommendationGeneratorForPatientUseCase recommendationGeneratorForPatientUseCase;

    public void handle(FHIRNotificationEvent notification) throws FHIRServerException {
        switch (notification.operationType) {
            case "create":
                recommendationGeneratorForPatientUseCase.generate(notification.getResourceId());
                break;
            // TODO: Add other cases if necessary
            default:
                Log.error("FHIRNotificationEvent with unknown operation type '" + notification.operationType + "'. Ignoring notification.");
        }
    }

}
