package org.fhirvp.api.notification;

import org.fhirvp.notification.NotificationHandler;

import javax.inject.Inject;
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
    public Response createNotification(String notificationJson) {
        notificationHandler.handleNotification(notificationJson);
        return Response.ok(notificationJson).build();
    }

}
