package org.fhirvp.model.mapper;

import com.ibm.fhir.model.type.Address;
import org.fhirvp.Constants;

public class AddressMapped extends FHIRResourceMapped<Address> {

    AddressMapped(Address address) {
        super(address);
    }

    public String getId() {
        return _raw.getId();
    }

    public String getStateCode() {
        String fhirPath = "state.extension.where(url = '" + Constants.PROFILE_BASE_URL + "vp-state-code-extension').value.coding.where(system = 'urn:iso:std:iso:3166:-2').code";
        return createCodeValue(_raw, fhirPath);
    }

    public String getCountryCode() {
        String fhirPath = "country.extension.where(url = '" + Constants.PROFILE_BASE_URL + "vp-country-code-extension').value.coding.where(system = 'urn:iso:std:iso:3166').code";
        return createCodeValue(_raw, fhirPath);
    }

}
