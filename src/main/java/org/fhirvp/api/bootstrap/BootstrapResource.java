package org.fhirvp.api.bootstrap;

import org.fhirvp.api.bootstrap.dto.BootstrapResponse;
import org.fhirvp.usecase.BootstrapUseCase;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/bootstrap")
public class BootstrapResource {
    @Inject
    BootstrapUseCase bootstrapUseCase;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public BootstrapResponse bootstrap() throws Exception {
        var bootstrapResult = bootstrapUseCase.bootstrap();
        return BootstrapResponse.from(bootstrapResult);
    }
}
