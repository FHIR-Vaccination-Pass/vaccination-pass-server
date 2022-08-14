package org.fhirvp.api.notification;

import org.fhirvp.notification.FHIRNotificationEvent;
import org.fhirvp.notification.NotificationHandler;

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
    NotificationHandler notificationHandler;

    @POST
    public Response createNotification(@Valid FHIRNotificationEvent notification) {
        notificationHandler.handleNotification(notification);
        return Response.ok().build();
    }

}
