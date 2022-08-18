package org.fhirvp.model.mapper;

import com.ibm.fhir.model.resource.Basic;
import com.ibm.fhir.model.type.*;
import org.junit.jupiter.api.Test;

import java.lang.String;

class VaccinationDoseMappedTest {

    static final String BASE_URL = "https://fhir-vaccination-pass.github.io/fhir-implementation-guide/StructureDefinition/";
    static final String PROFILE_URL = BASE_URL + "vp-vaccination-dose";

    @Test
    void testSingleDose() {
        VaccinationDoseMapped vaccinationDoseMapped = new VaccinationDoseMapped(generateVaccinationDoseSingle());

        assert vaccinationDoseMapped.getVaccinationSchemeId().equals("VaccinationScheme123");
        assert vaccinationDoseMapped.isProtected();
        assert vaccinationDoseMapped.getVaccinationDoseSingleExtension().get().getNumberInScheme() == 2;
        assert vaccinationDoseMapped.getVaccinationDoseSingleExtension().get().getTimeframeStart().get().toDays() == 1 * 30;
        assert vaccinationDoseMapped.getVaccinationDoseSingleExtension().get().getTimeframeEnd().get().toDays() == 3 * 30;
    }

    @Test
    void testRepeatingDose() {
        VaccinationDoseMapped vaccinationDoseMapped = new VaccinationDoseMapped(generateVaccinationDoseRepeating());

        assert vaccinationDoseMapped.getVaccinationSchemeId().equals("VaccinationScheme123");
        assert vaccinationDoseMapped.isProtected();
        assert vaccinationDoseMapped.getVaccinationDoseRepeatingMapped().get().getInterval().toDays() == 5 * 365;
    }

    private Basic generateVaccinationDoseSingle() {
        return Basic.builder()
                .meta(Meta.builder()
                        .profile(Canonical.of(PROFILE_URL))
                        .build())
                .code(CodeableConcept.builder()
                        .coding(Coding.builder()
                                .code(Code.builder()
                                        .value("VaccinationDose")
                                        .build())
                                .build())
                        .build())
                .subject(Reference.builder()
                        .reference("https://example.com/Basic/VaccinationScheme123")
                        .build())
                .extension(Extension.builder()
                        .url(BASE_URL + "vp-vaccination-dose-base-extension")
                        .extension(
                                Extension.builder()
                                        .url("doseQuantity")
                                        .value(Quantity.builder()
                                                .code(Code.of("ml"))
                                                .system(Uri.of("http://unitsofmeasure.org"))
                                                .value(Decimal.of(0.5))
                                                .build())
                                        .build(),
                                Extension.builder()
                                        .url("isProtected")
                                        .value(true)
                                        .build(),
                                Extension.builder()
                                        .url("notes")
                                        .value(Markdown.of("This is a note."))
                                        .build()
                        )
                        .build())
                .extension(Extension.builder()
                        .url(BASE_URL + "vp-vaccination-dose-single-extension")
                        .extension(
                                Extension.builder()
                                        .url("numberInScheme")
                                        .value(UnsignedInt.of(2))
                                        .build(),
                                Extension.builder()
                                        .url("timeframeStart")
                                        .value(Quantity.builder()
                                                .code(Code.of("mo"))
                                                .system(Uri.of("http://unitsofmeasure.org"))
                                                .value(Decimal.of(1))
                                                .build())
                                        .build(),
                                Extension.builder()
                                        .url("timeframeEnd")
                                        .value(Quantity.builder()
                                                .code(Code.of("mo"))
                                                .system(Uri.of("http://unitsofmeasure.org"))
                                                .value(Decimal.of(3))
                                                .build())
                                        .build()
                        )
                        .build())
                .build();
    }

    private Basic generateVaccinationDoseRepeating() {
        return Basic.builder()
                .meta(Meta.builder()
                        .profile(Canonical.of(PROFILE_URL))
                        .build())
                .code(CodeableConcept.builder()
                        .coding(Coding.builder()
                                .code(Code.builder()
                                        .value("VaccinationDose")
                                        .build())
                                .build())
                        .build())
                .subject(Reference.builder()
                        .reference("https://example.com/Basic/VaccinationScheme123")
                        .build())
                .extension(Extension.builder()
                        .url(BASE_URL + "vp-vaccination-dose-base-extension")
                        .extension(
                                Extension.builder()
                                        .url("doseQuantity")
                                        .value(Quantity.builder()
                                                .code(Code.of("ml"))
                                                .system(Uri.of("http://unitsofmeasure.org"))
                                                .value(Decimal.of(0.5))
                                                .build())
                                        .build(),
                                Extension.builder()
                                        .url("isProtected")
                                        .value(true)
                                        .build(),
                                Extension.builder()
                                        .url("notes")
                                        .value(Markdown.of("This is a note."))
                                        .build()
                        )
                        .build())
                .extension(Extension.builder()
                        .url(BASE_URL + "vp-vaccination-dose-repeating-extension")
                        .extension(Extension.builder()
                                        .url("interval")
                                        .value(Quantity.builder()
                                                .code(Code.of("a"))
                                                .system(Uri.of("http://unitsofmeasure.org"))
                                                .value(Decimal.of(5))
                                                .build())
                                        .build())
                        .build())
                .build();
    }

}
