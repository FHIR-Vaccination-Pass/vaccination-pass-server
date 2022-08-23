package org.fhirvp.model.mapper;

import com.ibm.fhir.model.resource.Basic;
import com.ibm.fhir.model.type.*;
import org.junit.jupiter.api.Test;

import java.lang.String;

class PopulationRecommendationMappedTest {

    static final String BASE_URL = "https://fhir-vaccination-pass.github.io/fhir-implementation-guide/StructureDefinition/";
    static final String PROFILE_URL = BASE_URL + "vp-population-recommendation";

    @Test
    void test() {
        PopulationRecommendationMapped populationRecommendationMapped = new PopulationRecommendationMapped(generatePopulationRecommendation());

        assert populationRecommendationMapped.getTargetDiseaseCode().equals("disease-abc");
        assert populationRecommendationMapped.getAgeStartInDays().get() == 12 * 365;
        assert populationRecommendationMapped.getAgeEndInDays().get() == 42 * 365;
        assert populationRecommendationMapped.getLocationsMapped().get(0).getCountryCode().equals("DE");
        assert populationRecommendationMapped.getLocationsMapped().get(0).getStateCode().get().equals("DE-BY");
    }

    private Basic generatePopulationRecommendation() {
        return Basic.builder()
                .meta(Meta.builder()
                        .profile(Canonical.of(PROFILE_URL))
                        .build())
                .code(CodeableConcept.builder()
                        .coding(Coding.builder()
                                .code(Code.builder()
                                        .value("PopulationRecommendation")
                                        .build())
                                .build())
                        .build())
                .extension(Extension.builder()
                        .url(BASE_URL + "vp-population-recommendation-extension")
                        .extension(
                                Extension.builder()
                                        .url("targetDisease")
                                        .value(CodeableConcept.builder()
                                                .coding(Coding.builder()
                                                        .system(Uri.builder()
                                                                .value("http://hl7.org/fhir/sid/icd-10")
                                                                .build())
                                                        .code(Code.builder()
                                                                .value("disease-abc")
                                                                .build())
                                                        .build())
                                                .build())
                                        .build(),
                                Extension.builder()
                                        .url("ageStart")
                                        .value(Age.builder()
                                                .code(Code.builder()
                                                        .value("a")
                                                        .build())
                                                .system(Uri.of("http://unitsofmeasure.org"))
                                                .value(Decimal.of(12))
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
                                                .build())
                                        .build(),
                                Extension.builder()
                                        .url(BASE_URL + "vp-location-extension")
                                        .extension(
                                                Extension.builder()
                                                        .url(BASE_URL + "vp-country-code-extension")
                                                        .value(CodeableConcept.builder()
                                                                .coding(Coding.builder()
                                                                        .system(Uri.of("urn:iso:std:iso:3166"))
                                                                        .code(Code.of("DE"))
                                                                        .build())
                                                                .build())
                                                        .build(),
                                                Extension.builder()
                                                        .url(BASE_URL + "vp-state-code-extension")
                                                        .value(CodeableConcept.builder()
                                                                .coding(Coding.builder()
                                                                        .system(Uri.of("urn:iso:std:iso:3166:-2"))
                                                                        .code(Code.of("DE-BY"))
                                                                        .build())
                                                                .build())
                                                        .build()
                                        )
                                        .build()
                        )
                        .build())
                .build();
    }

}
