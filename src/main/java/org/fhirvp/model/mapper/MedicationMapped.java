package org.fhirvp.model.mapper;

import com.ibm.fhir.model.resource.Medication;
import com.ibm.fhir.model.type.Code;
import io.vavr.control.Try;
import org.fhirvp.Constants;

import java.util.List;
import java.util.stream.Collectors;

public class MedicationMapped extends FHIRResourceMapped<Medication> {

    public MedicationMapped(Medication medication) {
        super(medication);
    }

    public String getId() {
        return _raw.getId();
    }

    public String getCode() {
        String fhirPath = "code.coding.where(system = 'http://fhir.de/CodeSystem/ifa/pzn').code";
        return super.createCodeValue(_raw, fhirPath);
    }

    public String getTradeName() {
        String fhirPath = "extension.where(url = '" + Constants.PROFILE_BASE_URL + "vp-medication-trade-name').value";
        return super.createExactlyOne(_raw, fhirPath)
                .as(com.ibm.fhir.model.type.String.class).getValue();
    }

    public List<String> getTargetDiseaseCodes() {
        String fhirPath = "extension.where(url = '" + Constants.PROFILE_BASE_URL + "vp-medication-target-disease').value.coding.where(system = 'http://hl7.org/fhir/sid/icd-10').code";
        return Try.of(() -> fhirPathEvaluator.evaluate(_raw, fhirPath)).get()
                .stream().map(x -> x.asElementNode().element().as(Code.class).getValue())
                .collect(Collectors.toList());
    }

}
