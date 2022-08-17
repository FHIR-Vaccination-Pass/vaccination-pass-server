package org.fhirvp.model.mapper;

import com.ibm.fhir.model.resource.Medication;
import com.ibm.fhir.model.type.*;
import org.junit.jupiter.api.Test;

import java.lang.String;

class MedicationMappedTest {

    static final String BASE_URL = "https://fhir-vaccination-pass.github.io/fhir-implementation-guide/StructureDefinition/";
    static final String PROFILE_URL = BASE_URL + "vp-medication";

    @Test
    void test() {
        MedicationMapped medicationMapped = new MedicationMapped(generateMedication());

        assert medicationMapped.getCode().equals("vaccine-abc");
        assert medicationMapped.getTradeName().equals("vaccine-name-abc");
        assert medicationMapped.getTargetDiseaseCodes().get(0).equals("A00");
        assert medicationMapped.getTargetDiseaseCodes().get(1).equals("A01");
    }

    private Medication generateMedication() {
        return Medication.builder()
                .meta(Meta.builder()
                        .profile(Canonical.of(PROFILE_URL))
                        .build())
                .code(CodeableConcept.builder()
                        .coding(Coding.builder()
                                .system(Uri.builder()
                                        .value("http://fhir.de/CodeSystem/ifa/pzn")
                                        .build())
                                .code(Code.builder()
                                        .value("vaccine-abc")
                                        .build())
                                .build())
                        .build())
                .manufacturer(Reference.builder()
                        .reference("https://example.com/Organization/123")
                        .build())
                .form(CodeableConcept.builder()
                        .coding(Coding.builder()
                                .system(Uri.builder()
                                        .value("http://snomed.info/sct")
                                        .build())
                                .code(Code.builder()
                                        .value("736542009")
                                        .build())
                                .build())
                        .build())
                .extension(Extension.builder()
                        .url(BASE_URL + "vp-medication-trade-name")
                        .value("vaccine-name-abc")
                        .build())
                .extension(
                        Extension.builder()
                                .url(BASE_URL + "vp-medication-target-disease")
                                .value(CodeableConcept.builder()
                                        .coding(Coding.builder()
                                                .system(Uri.builder()
                                                        .value("http://hl7.org/fhir/sid/icd-10")
                                                        .build())
                                                .code(Code.builder()
                                                        .value("A00")
                                                        .build())
                                                .build())
                                        .build())
                                .build(),
                        Extension.builder()
                                .url(BASE_URL + "vp-medication-target-disease")
                                .value(CodeableConcept.builder()
                                        .coding(Coding.builder()
                                                .system(Uri.builder()
                                                        .value("http://hl7.org/fhir/sid/icd-10")
                                                        .build())
                                                .code(Code.builder()
                                                        .value("A01")
                                                        .build())
                                                .build())
                                        .build())
                                .build()
                )
                .build();
    }

}
