package org.fhirvp.model.mapper;

import com.ibm.fhir.model.resource.Basic;
import com.ibm.fhir.model.type.*;
import org.junit.jupiter.api.Test;

import java.lang.String;

class TargetDiseaseMappedTest {

    static final String PROFILE_URL = "https://fhir-vaccination-pass.github.io/fhir-implementation-guide/StructureDefinition/vp-target-disease";

    @Test
    void test() {
        TargetDiseaseMapped targetDiseaseMapped = new TargetDiseaseMapped(generateTargetDisease());

        assert targetDiseaseMapped.getCode().equals("U07.1");
        assert targetDiseaseMapped.getName().equals("SARS-COV-19");
    }

    private Basic generateTargetDisease() {
        return Basic.builder()
                .meta(Meta.builder()
                        .profile(Canonical.of(PROFILE_URL))
                        .build())
                .code(CodeableConcept.builder()
                        .coding(Coding.builder()
                                .code(Code.builder()
                                        .value("TargetDisease")
                                        .build())
                                .build())
                        .build())
                .extension(Extension.builder()
                        .url("https://fhir-vaccination-pass.github.io/fhir-implementation-guide/StructureDefinition/vp-target-disease-extension")
                        .extension(
                                Extension.builder()
                                        .url("code")
                                        .value(CodeableConcept.builder()
                                                .coding(Coding.builder()
                                                        .system(Uri.of("http://hl7.org/fhir/sid/icd-10"))
                                                        .code(Code.of("U07.1"))
                                                        .build())
                                                .build())
                                        .build(),
                                Extension.builder()
                                        .url("name")
                                        .value("SARS-COV-19")
                                        .build(),
                                Extension.builder()
                                        .url("description")
                                        .value(Markdown.of("This is bad for you."))
                                        .build()
                        )
                        .build())
                .build();
    }

}
