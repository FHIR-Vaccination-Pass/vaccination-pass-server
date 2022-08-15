package org.fhirvp.api.notification;

import org.fhirvp.model.FHIRNotificationEvent;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;
import org.fhirvp.usecase.NotificationUseCase;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/notification")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FHIRNotificationResource {

    @Inject
    NotificationUseCase notificationUseCase;

    @POST
    public Response createNotification(@Valid FHIRNotificationEvent notification) throws FHIRServerException {
        notificationUseCase.handleNotification(notification);
        return Response.ok().build();
    }

}
