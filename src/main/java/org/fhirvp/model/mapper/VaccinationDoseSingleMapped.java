package org.fhirvp.model.mapper;

import com.ibm.fhir.model.type.Extension;

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

    public Optional<Long> getTimeframeStartInDays() {
        String fhirPath = "extension.where(url = 'timeframeStart').value";
        return super.createAgeInDays(_raw, fhirPath);
    }

    public Optional<Long> getTimeframeEndInDays() {
        String fhirPath = "extension.where(url = 'timeframeEnd').value";
        return super.createAgeInDays(_raw, fhirPath);
    }

}
