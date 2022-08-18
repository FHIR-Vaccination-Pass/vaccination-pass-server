package org.fhirvp.model.mapper;

import com.ibm.fhir.model.resource.Basic;
import com.ibm.fhir.model.type.*;
import org.junit.jupiter.api.Test;

import java.lang.String;

class ActiveVaccinationSchemeMappedTest {

    static final String BASE_URL = "https://fhir-vaccination-pass.github.io/fhir-implementation-guide/StructureDefinition/";
    static final String PROFILE_URL = BASE_URL + "vp-active-vaccination-scheme";

    @Test
    void test() {
        ActiveVaccinationSchemeMapped activeVaccinationSchemeMapped = new ActiveVaccinationSchemeMapped(generateActiveVaccinationScheme());

        assert activeVaccinationSchemeMapped.getPatientId().equals("123");
        assert activeVaccinationSchemeMapped.getVaccinationSchemeId().equals("456");
    }

    private Basic generateActiveVaccinationScheme() {
        return Basic.builder()
                .meta(Meta.builder()
                        .profile(Canonical.of(PROFILE_URL))
                        .build())
                .code(CodeableConcept.builder()
                        .coding(Coding.builder()
                                .code(Code.builder()
                                        .value("ActiveVaccinationScheme")
                                        .build())
                                .build())
                        .build())
                .subject(Reference.builder()
                        .reference("https://example.com/Patient/123")
                        .build())
                .extension(Extension.builder()
                        .url(BASE_URL + "vp-active-vaccination-scheme-extension")
                        .extension(
                                Extension.builder()
                                        .url("vaccinationScheme")
                                        .value(Reference.builder()
                                                .reference("https://example.com/Basic/456")
                                                .build())
                                        .build(),
                                Extension.builder()
                                        .url("changeReason")
                                        .value(Markdown.of("This is a reason."))
                                        .build()
                        )
                        .build())
                .build();
    }

}
