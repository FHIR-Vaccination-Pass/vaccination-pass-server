package org.fhirvp.usecase.impl.notification;

import com.ibm.fhir.model.resource.Bundle;
import com.ibm.fhir.model.resource.Patient;
import io.quarkus.logging.Log;
import org.fhirvp.model.FHIRNotificationEvent;
import org.fhirvp.ports.ActiveVaccinationSchemePort;
import org.fhirvp.ports.PatientPort;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;
import org.fhirvp.usecase.OnPatientNotificationUseCase;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class OnPatientNotificationUseCaseImpl implements OnPatientNotificationUseCase {

    @Inject
    PatientPort patientPort;

    @Inject
    ActiveVaccinationSchemePort activeVaccinationSchemePort;

    public void handle(FHIRNotificationEvent notification) throws FHIRServerException {
        switch (notification.operationType) {
            case "create":
                patientCreated(notification);
                break;
            // TODO: Add other cases if necessary
            default:
                Log.error("FHIRNotificationEvent with unknown operation type '" + notification.operationType + "'. Ignoring notification.");
        }
    }

    public void patientCreated(FHIRNotificationEvent notification) throws FHIRServerException {
        // TODO: Change. This is just for demonstration purposes.
        Patient patient = patientPort.read(notification.resourceId);
        Bundle bundle = activeVaccinationSchemePort.search(null);
        System.out.println(bundle);
    }

}
