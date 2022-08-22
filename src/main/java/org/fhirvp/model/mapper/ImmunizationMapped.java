package org.fhirvp.model.mapper;

import com.ibm.fhir.model.resource.Immunization;
import com.ibm.fhir.model.type.Reference;
import com.ibm.fhir.model.type.code.ImmunizationStatus;
import org.fhirvp.Constants;

import java.time.LocalDate;

public class ImmunizationMapped extends FHIRResourceMapped<Immunization> {

    public ImmunizationMapped(Immunization immunization) {
        super(immunization);
    }

    public String getId() {
        return _raw.getId();
    }

    public ImmunizationStatus getStatus() {
        return _raw.getStatus();
    }

    public String getVaccineCode() {
        String fhirPath = "vaccineCode.coding.where(system = 'http://fhir.de/CodeSystem/ifa/pzn').code";
        return super.createCodeValue(_raw, fhirPath);
    }

    public String getPatientId() {
        String[] referenceParts = _raw.getPatient().getReference().getValue().split("/");
        return referenceParts[referenceParts.length - 1];
    }

    public LocalDate getOccurrence() {
        return LocalDate.from(_raw.getOccurrence().as(com.ibm.fhir.model.type.DateTime.class).getValue());
    }

    public String getAdministeredVaccinationDoseId() {
        String fhirPath = "extension.where(url = '" + Constants.PROFILE_BASE_URL + "vp-administered-vaccination-dose').value";
        String[] referenceParts = super.createExactlyOne(_raw, fhirPath)
                .as(Reference.class).getReference().getValue().split("/");
        return referenceParts[referenceParts.length - 1];
    }

}
