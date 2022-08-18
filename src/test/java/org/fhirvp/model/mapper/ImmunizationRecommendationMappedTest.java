package org.fhirvp.model.mapper;

import com.ibm.fhir.model.resource.ImmunizationRecommendation;
import com.ibm.fhir.model.type.*;
import org.fhirvp.model.ForecastStatus;
import org.junit.jupiter.api.Test;

import java.lang.String;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

class ImmunizationRecommendationMappedTest {

    static final String BASE_URL = "https://fhir-vaccination-pass.github.io/fhir-implementation-guide/StructureDefinition/";
    static final String PROFILE_URL = BASE_URL + "vp-immunization-recommendation";

    @Test
    void test() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        ImmunizationRecommendationMapped immunizationRecommendationMapped = new ImmunizationRecommendationMapped(generateImmunizationRecommendation());

        assert immunizationRecommendationMapped.getPatientId().equals("123");
        assert immunizationRecommendationMapped.getCreatedDate().format(dateTimeFormatter).equals("10.07.2022");
        assert immunizationRecommendationMapped.getVaccineCode().equals("vaccine-abc");
        assert immunizationRecommendationMapped.getTargetDiseaseCode().equals("disease-abc");
        assert immunizationRecommendationMapped.getForecastStatus().equals(ForecastStatus.COMPLETE);
        assert immunizationRecommendationMapped.getDateToGive().isEmpty();
        assert immunizationRecommendationMapped.getEarliestDateToGive().get().format(dateTimeFormatter).equals("14.07.2022");
        assert immunizationRecommendationMapped.getLatestDateToGive().get().format(dateTimeFormatter).equals("21.07.2022");
        assert immunizationRecommendationMapped.getSupportingImmunizationIds().get(0).equals("456");
        assert !immunizationRecommendationMapped.isDeactivated();
        assert immunizationRecommendationMapped.getFulfillingImmunizationId().get().equals("789");
        assert immunizationRecommendationMapped.getSupportingPopulationRecommendationId().equals("321");
        assert immunizationRecommendationMapped.getRecommendedVaccinationDoseId().equals("654");
    }

    private ImmunizationRecommendation generateImmunizationRecommendation() {
        return ImmunizationRecommendation.builder()
                .meta(Meta.builder()
                        .profile(Canonical.of(PROFILE_URL))
                        .build())
                .patient(Reference.builder()
                        .reference("https://example.com/Patient/123")
                        .build())
                .date(DateTime.of(LocalDate.of(2022, 7, 10)))
                .recommendation(ImmunizationRecommendation.Recommendation.builder()
                        .vaccineCode(CodeableConcept.builder()
                                .coding(Coding.builder()
                                        .system(Uri.builder()
                                                .value("http://fhir.de/CodeSystem/ifa/pzn")
                                                .build())
                                        .code(Code.builder()
                                                .value("vaccine-abc")
                                                .build())
                                        .build())
                                .build())
                        .targetDisease(CodeableConcept.builder()
                                .coding(Coding.builder()
                                        .system(Uri.builder()
                                                .value("http://hl7.org/fhir/sid/icd-10")
                                                .build())
                                        .code(Code.builder()
                                                .value("disease-abc")
                                                .build())
                                        .build())
                                .build())
                        .forecastStatus(CodeableConcept.builder()
                                .coding(Coding.builder()
                                        .system(Uri.builder()
                                                .value("http://terminology.hl7.org/CodeSystem/immunization-recommendation-status")
                                                .build())
                                        .code(Code.builder()
                                                .value("complete")
                                                .build())
                                        .build())
                                .build())
                        .dateCriterion(
                                ImmunizationRecommendation.Recommendation.DateCriterion.builder()
                                        .code(CodeableConcept.builder()
                                                .coding(Coding.builder()
                                                        .system(Uri.builder()
                                                                .value("http://loinc.org")
                                                                .build())
                                                        .code(Code.builder()
                                                                .value("30981-5") // Earliest date to give
                                                                .build())
                                                        .build())
                                                .build())
                                        .value(DateTime.of(LocalDate.of(2022, 7, 14)))
                                        .build(),
                                ImmunizationRecommendation.Recommendation.DateCriterion.builder()
                                        .code(CodeableConcept.builder()
                                                .coding(Coding.builder()
                                                        .system(Uri.builder()
                                                                .value("http://loinc.org")
                                                                .build())
                                                        .code(Code.builder()
                                                                .value("59777-3") // Latest date to give immunization
                                                                .build())
                                                        .build())
                                                .build())
                                        .value(DateTime.of(LocalDate.of(2022, 7, 21)))
                                        .build()
                        )
                        .supportingImmunization(Reference.builder()
                                .reference("https://example.com/Immunization/456")
                                .build())
                        .build())
                .extension(Extension.builder()
                        .url(BASE_URL + "vp-immunization-recommendation-is-deactivated")
                        .value(false)
                        .build())
                .extension(Extension.builder()
                        .url(BASE_URL + "vp-fulfilling-immunization")
                        .value(Reference.builder()
                                .reference("https://example.com/Immunization/789")
                                .build())
                        .build())
                .extension(Extension.builder()
                        .url(BASE_URL + "vp-supporting-population-recommendation")
                        .value(Reference.builder()
                                .reference("https://example.com/Basic/321")
                                .build())
                        .build())
                .extension(Extension.builder()
                        .url(BASE_URL + "vp-recommended-vaccination-dose")
                        .value(Reference.builder()
                                .reference("https://example.com/Basic/654")
                                .build())
                        .build())
                .build();
    }

}
