package org.fhirvp.model.mapper;

import com.ibm.fhir.model.resource.Basic;
import com.ibm.fhir.model.type.Extension;
import com.ibm.fhir.model.type.Reference;
import org.fhirvp.Constants;

public class ActiveVaccinationSchemeMapped extends FHIRResourceMapped<Basic> {

    private final Extension activeVaccinationSchemeExtension;

    public ActiveVaccinationSchemeMapped(Basic basic) {
        super(basic);
        this.activeVaccinationSchemeExtension = createActiveVaccinationSchemeExtension();
    }

    private Extension createActiveVaccinationSchemeExtension() {
        String fhirPath = "extension.where(url = '" + Constants.PROFILE_BASE_URL + "vp-active-vaccination-scheme-extension')";
        return super.createBaseExtension(fhirPath);
    }

    public String getId() {
        return _raw.getId();
    }

    public String getPatientId() {
        String[] referenceParts = _raw.getSubject().getReference().getValue().split("/");
        return referenceParts[referenceParts.length - 1];
    }

    public String getVaccinationSchemeId() {
        String fhirPath = "extension.where(url = 'vaccinationScheme').value";
        String[] referenceParts = super.createExactlyOne(activeVaccinationSchemeExtension, fhirPath)
                .as(Reference.class).getReference().getValue().split("/");
        return referenceParts[referenceParts.length - 1];
    }

}
