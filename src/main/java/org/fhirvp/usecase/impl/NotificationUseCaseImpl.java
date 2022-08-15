package org.fhirvp.usecase.impl;

import com.ibm.fhir.model.resource.Basic;
import com.ibm.fhir.model.type.Uri;
import io.quarkus.arc.Unremovable;
import io.quarkus.logging.Log;
import org.fhirvp.Constants;
import org.fhirvp.model.FHIRNotificationEvent;
import org.fhirvp.ports.BasicPort;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;
import org.fhirvp.usecase.NotificationUseCase;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
@Unremovable // This bean is used in FHIRNotificationClientEndpoint without @Inject so Quarkus would remove it. @Unremovable prevents this.
public class NotificationUseCaseImpl implements NotificationUseCase {

    @Inject
    BasicPort basicPort;

    @Inject
    PatientNotification patientNotification;

    @Override
    public void handleNotification(FHIRNotificationEvent notification) throws FHIRServerException {
        switch (notification.getResourceTypeString()) {
            case "Patient":
                patientNotification.handle(notification);
                break;
            case "Practitioner":
                break;
            case "Immunization":
                break;
            case "ImmunizationRecommendation":
                break;
            case "Medication":
                break;
            case "Organization":
                break;
            case "Basic":
                handleBasicResourceNotification(notification);
                break;
            default:
                Log.info("Received FHIR notification with unsupported resource type. Ignoring notification.");
        }
    }

    private void handleBasicResourceNotification(FHIRNotificationEvent notification) throws FHIRServerException {
        Basic basic = basicPort.read(notification.resourceId);
        String resourceTypeString = basic.getMeta().getProfile().stream()
                .map(Uri::getValue)
                .filter(uri -> uri.startsWith(Constants.PROFILE_BASE_URL))
                .map(uri -> uri.substring(Constants.PROFILE_BASE_URL.length()))
                .findFirst()
                .orElse("UNKNOWN");
        switch (resourceTypeString) {
            case "vp-vacation-plan":
                break;
            case "vp-active-vaccination-scheme":
                break;
            case "vp-population-recommendation":
                break;
            case "vp-target-disease":
                break;
            case "vp-vaccination-scheme":
                break;
            case "vp-vaccination-dose":
                break;
            default:
                Log.info("Received FHIR notification with unsupported Basic resource. Ignoring notification.");
                break;
        }
    }

}
