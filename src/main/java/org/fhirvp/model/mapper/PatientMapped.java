package org.fhirvp.model.mapper;

import com.ibm.fhir.model.resource.Patient;
import com.ibm.fhir.model.type.Element;
import org.fhirvp.Constants;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class PatientMapped extends FHIRResourceMapped<Patient> {

    private final List<AddressMapped> addressMappers;

    public PatientMapped(Patient patient) {
        super(patient);
        this.addressMappers = createAddressMappers();
    }

    private List<AddressMapped> createAddressMappers() {
        return _raw.getAddress().stream().map(AddressMapped::new).collect(Collectors.toList());
    }

    public String getId() {
        return _raw.getId();
    }

    public boolean isActive() {
        return _raw.getActive().getValue();
    }

    public Calendar getBirthDate() {
        return convertFullDateToCalendar(_raw.getBirthDate().getValue());
    }

    public boolean isDeceased() {
        Element deceased = _raw.getDeceased();
        if (deceased.getClass() == com.ibm.fhir.model.type.Boolean.class) {
            return deceased.as(com.ibm.fhir.model.type.Boolean.class).getValue();
        } else {
            return deceased.as(com.ibm.fhir.model.type.DateTime.class).hasValue();
        }
    }

    public boolean isPregnant() {
        String fhirPath = "extension.where(url = '"+ Constants.PROFILE_BASE_URL + "vp-patient-is-pregnant-extension').value";
        return super.createExactlyOne(_raw, fhirPath)
                .as(com.ibm.fhir.model.type.Boolean.class).getValue();
    }

    public String getKeycloakUsername() {
        String fhirPath = "extension.where(url = '" + Constants.PROFILE_BASE_URL + "vp-patient-keycloak-username-extension').value";
        return createExactlyOne(_raw, fhirPath)
                .as(com.ibm.fhir.model.type.String.class).getValue();
    }

    public List<AddressMapped> getAddressMappers() {
        return addressMappers;
    }

}
