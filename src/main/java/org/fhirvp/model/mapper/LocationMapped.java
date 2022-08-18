package org.fhirvp.model.mapper;

import com.ibm.fhir.model.type.Extension;
import com.ibm.fhir.path.FHIRPathNode;
import org.fhirvp.Constants;

import java.util.Optional;

public class LocationMapped extends FHIRResourceMapped<Extension> {

    LocationMapped(Extension extension) {
        super(extension);
    }

    public String getId() {
        return _raw.getId();
    }

    public String getCountryCode() {
        String fhirPath = "extension.where(url = '" + Constants.PROFILE_BASE_URL + "vp-country-code-extension').value.coding.where(system = 'urn:iso:std:iso:3166').code";
        return createCodeValue(_raw, fhirPath);
    }

    public Optional<String> getStateCode() {
        String fhirPath = "extension.where(url = '" + Constants.PROFILE_BASE_URL + "vp-state-code-extension').value.coding.where(system = 'urn:iso:std:iso:3166:-2').code";
        Optional<FHIRPathNode> optionalNode = super.createOptionalOne(_raw, fhirPath);
        if (optionalNode.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(optionalNode.get().asElementNode().element().as(com.ibm.fhir.model.type.String.class).getValue());
    }

}
