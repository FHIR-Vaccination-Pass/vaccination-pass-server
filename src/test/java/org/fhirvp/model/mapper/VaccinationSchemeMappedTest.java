package org.fhirvp.model.mapper;

import com.ibm.fhir.model.resource.Basic;
import com.ibm.fhir.model.type.*;
import org.fhirvp.model.VaccinationSchemeType;
import org.junit.jupiter.api.Test;

import java.lang.String;

class VaccinationSchemeMappedTest {

    static final String PROFILE_URL = "https://fhir-vaccination-pass.github.io/fhir-implementation-guide/StructureDefinition/vp-vaccination-scheme";

    @Test
    void test() {
        VaccinationSchemeMapped vaccinationSchemeMapped = new VaccinationSchemeMapped(generateVaccinationScheme());

        assert vaccinationSchemeMapped.getMedicationId().equals("123");
        assert vaccinationSchemeMapped.getName().equals("MyFirstVaccinationScheme");
        assert vaccinationSchemeMapped.getType().equals(VaccinationSchemeType.STANDARD);
        assert vaccinationSchemeMapped.isPreferred();
        assert vaccinationSchemeMapped.getAgeStartInDays().get() == 12 * 365;
        assert vaccinationSchemeMapped.getAgeEndInDays().get() == 42 * 365;
    }

    private Basic generateVaccinationScheme() {
        return Basic.builder()
                .meta(Meta.builder()
                        .profile(Canonical.of(PROFILE_URL))
                        .build())
                .code(CodeableConcept.builder()
                        .coding(Coding.builder()
                                .code(Code.builder()
                                        .value("VaccinationScheme")
                                        .build())
                                .build())
                        .build())
                .subject(Reference.builder()
                        .reference("https://example.com/Medication/123")
                        .build())
                .extension(Extension.builder()
                        .url("https://fhir-vaccination-pass.github.io/fhir-implementation-guide/StructureDefinition/vp-vaccination-scheme-extension")
                        .extension(
                                Extension.builder()
                                        .url("name")
                                        .value("MyFirstVaccinationScheme")
                                        .build(),
                                Extension.builder()
                                        .url("type")
                                        .value("standard")
                                        .build(),
                                Extension.builder()
                                        .url("isPreferred")
                                        .value(true)
                                        .build(),
                                Extension.builder()
                                        .url("ageStart")
                                        .value(Age.builder()
                                                .code(Code.builder()
                                                        .value("a")
                                                        .build())
                                                .system(Uri.of("http://unitsofmeasure.org"))
                                                .value(Decimal.of(12))
                                                .unit("yr")
                                                .build())
                                        .build(),
                                Extension.builder()
                                        .url("ageEnd")
                                        .value(Age.builder()
                                                .code(Code.builder()
                                                        .value("a")
                                                        .build())
                                                .system(Uri.of("http://unitsofmeasure.org"))
                                                .value(Decimal.of(42))
                                                .unit("yr")
                                                .build())
                                        .build()
                        )
                        .build())
                .build();
    }

}
