package org.fhirvp.notification;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FHIRNotificationEvent {

    private String lastUpdated = null;
    private String location = null;
    private String operationType = null;
    private String resourceId = null;
    private String tenantId = null;
    private String datasourceId = null;

}
