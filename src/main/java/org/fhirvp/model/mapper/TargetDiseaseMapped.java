package org.fhirvp.model.mapper;

import com.ibm.fhir.model.resource.Basic;
import com.ibm.fhir.model.type.Extension;
import org.fhirvp.Constants;

public class TargetDiseaseMapped extends FHIRResourceMapped<Basic> {

    private final Extension targetDiseaseExtension;

    public TargetDiseaseMapped(Basic basic) {
        super(basic);
        this.targetDiseaseExtension = createTargetDiseaseExtension();
    }

    private Extension createTargetDiseaseExtension() {
        String fhirPath = "extension.where(url = '" + Constants.PROFILE_BASE_URL + "vp-target-disease-extension')";
        return super.createBaseExtension(fhirPath);
    }

    public String getId() {
        return _raw.getId();
    }

    public String getCode() {
        String fhirPath = "extension.where(url = 'code').value.coding.where(system = 'http://hl7.org/fhir/sid/icd-10').code";
        return createCodeValue(targetDiseaseExtension, fhirPath);
    }

    public String getName() {
        String fhirPath = "extension.where(url = 'name').value";
        return super.createExactlyOne(targetDiseaseExtension, fhirPath)
                .as(com.ibm.fhir.model.type.String.class).getValue();
    }

}
