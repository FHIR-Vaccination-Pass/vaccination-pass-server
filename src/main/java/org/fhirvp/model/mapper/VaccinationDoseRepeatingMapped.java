package org.fhirvp.model.mapper;

import com.ibm.fhir.model.type.Extension;

import java.time.Duration;

public class VaccinationDoseRepeatingMapped extends FHIRResourceMapped<Extension> {

    public VaccinationDoseRepeatingMapped(Extension extension) {
        super(extension);
    }

    public Duration getInterval() {
        String fhirPath = "extension.where(url = 'interval').value";
        return super.createAge(_raw, fhirPath).orElseThrow();
    }

}
