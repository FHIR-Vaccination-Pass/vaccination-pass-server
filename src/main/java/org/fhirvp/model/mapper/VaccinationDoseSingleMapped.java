package org.fhirvp.model.mapper;

import com.ibm.fhir.model.type.Extension;

import java.time.Duration;
import java.util.Optional;

public class VaccinationDoseSingleMapped extends FHIRResourceMapped<Extension> {

    public VaccinationDoseSingleMapped(Extension extension) {
        super(extension);
    }

    public int getNumberInScheme() {
        String fhirPath = "extension.where(url = 'numberInScheme').value";
        return super.createExactlyOne(_raw, fhirPath)
                .as(com.ibm.fhir.model.type.UnsignedInt.class).getValue();
    }

    public Optional<Duration> getTimeframeStart() {
        String fhirPath = "extension.where(url = 'timeframeStart').value";
        return super.createAge(_raw, fhirPath);
    }

    public Optional<Duration> getTimeframeEnd() {
        String fhirPath = "extension.where(url = 'timeframeEnd').value";
        return super.createAge(_raw, fhirPath);
    }

}
