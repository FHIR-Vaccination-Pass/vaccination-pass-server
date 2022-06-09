package org.fhirvp.api.bootstrap.dto;

import com.ibm.fhir.model.resource.Patient;
import lombok.Value;

@Value
public class BootstrapResponse {
    String location;
    Patient entity;
}
