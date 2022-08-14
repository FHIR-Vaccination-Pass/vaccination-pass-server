package org.fhirvp.notification;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class FHIRNotificationEvent {

    @NotNull public String lastUpdated;
    @NotNull public String location;
    @NotNull public String operationType;
    @NotNull public String resourceId;
    @NotNull public String datasourceId;
    @NotNull public String tenantId;

}
