package org.fhirvp.model.mapper;

import com.ibm.fhir.model.type.Extension;

public class VaccinationDoseRepeatingMapped extends FHIRResourceMapped<Extension> {

    public VaccinationDoseRepeatingMapped(Extension extension) {
        super(extension);
    }

    public long getIntervalInDays() {
        String fhirPath = "extension.where(url = 'interval').value";
        return super.createAgeInDays(_raw, fhirPath).orElseThrow();
    }

}
