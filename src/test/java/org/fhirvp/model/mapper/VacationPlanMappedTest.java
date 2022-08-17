package org.fhirvp.model.mapper;

import com.ibm.fhir.model.resource.Basic;
import com.ibm.fhir.model.type.*;
import org.junit.jupiter.api.Test;

import java.lang.String;
import java.text.SimpleDateFormat;

class VacationPlanMappedTest {

    static final String BASE_URL = "https://fhir-vaccination-pass.github.io/fhir-implementation-guide/StructureDefinition/";
    static final String PROFILE_URL = BASE_URL + "vp-vacation-plan";

    @Test
    void test() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        VacationPlanMapped vacationPlanMapped = new VacationPlanMapped(generateVacationPlan());

        assert vacationPlanMapped.getPatientId().equals("123");
        assert dateFormat.format(vacationPlanMapped.getDepartureDate().getTime()).equals("01.01.2000");
        assert vacationPlanMapped.getLocationsMapped().get(0).getCountryCode().equals("DE");
        assert vacationPlanMapped.getLocationsMapped().get(0).getStateCode().get().equals("DE-BY");
    }

    private Basic generateVacationPlan() {
        return Basic.builder()
                .meta(Meta.builder()
                        .profile(Canonical.of(PROFILE_URL))
                        .build())
                .code(CodeableConcept.builder()
                        .coding(Coding.builder()
                                .code(Code.builder()
                                        .value("VacationPlan")
                                        .build())
                                .build())
                        .build())
                .subject(Reference.builder()
                        .reference("https://example.com/Patient/123")
                        .build())
                .extension(Extension.builder()
                        .url(BASE_URL + "vp-vacation-plan-extension")
                        .extension(Extension.builder()
                                .url(BASE_URL + "vp-location-extension")
                                .extension(Extension.builder()
                                        .url(BASE_URL + "vp-country-code-extension")
                                        .value(CodeableConcept.builder()
                                                .coding(Coding.builder()
                                                        .system(Uri.of("urn:iso:std:iso:3166"))
                                                        .code(Code.of("DE"))
                                                        .build())
                                                .build())
                                        .build())
                                .extension(Extension.builder()
                                        .url(BASE_URL + "vp-state-code-extension")
                                        .value(CodeableConcept.builder()
                                                .coding(Coding.builder()
                                                        .system(Uri.of("urn:iso:std:iso:3166:-2"))
                                                        .code(Code.of("DE-BY"))
                                                        .build())
                                                .build())
                                        .build())
                                .build())
                        .extension(Extension.builder()
                                .url("departureDate")
                                .value(Date.of("2000-01-01"))
                                .build())
                        .build())
                .build();
    }

}
