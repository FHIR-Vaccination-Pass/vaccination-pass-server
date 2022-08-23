package org.fhirvp.model.builder.impl;

import com.ibm.fhir.model.resource.Basic;
import com.ibm.fhir.model.resource.ImmunizationRecommendation;
import com.ibm.fhir.model.type.*;
import org.fhirvp.Constants;
import org.fhirvp.model.ForecastStatus;
import org.fhirvp.model.builder.FHIRResourceBuilder;

import javax.enterprise.context.ApplicationScoped;
import java.lang.String;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class FHIRResourceBuilderImpl implements FHIRResourceBuilder {

    public Basic buildActiveVaccinationScheme(String patientId, String vaccinationSchemeId, String changeReason) {
        return Basic.builder()
                .meta(Meta.builder()
                        .profile(Canonical.of(Constants.PROFILE_BASE_URL + "vp-active-vaccination-scheme"))
                        .build())
                .code(CodeableConcept.builder()
                        .coding(Coding.builder()
                                .code(Code.builder()
                                        .value("ActiveVaccinationScheme")
                                        .build())
                                .build())
                        .build())
                .subject(Reference.builder()
                        .reference("Patient/" + patientId)
                        .build())
                .extension(Extension.builder()
                        .url(Constants.PROFILE_BASE_URL + "vp-active-vaccination-scheme-extension")
                        .extension(
                                Extension.builder()
                                        .url("vaccinationScheme")
                                        .value(Reference.builder()
                                                .reference("Basic/" + vaccinationSchemeId)
                                                .build())
                                        .build(),
                                Extension.builder()
                                        .url("changeReason")
                                        .value(Markdown.of(changeReason))
                                        .build()
                        )
                        .build())
                .build();
    }

    public ImmunizationRecommendation buildImmunizationRecommendation
            (
                    String patientId,
                    String vaccineCode,
                    String targetDiseaseCode,
                    ForecastStatus forecastStatus,
                    Optional<LocalDate> dateToGive,
                    Optional<LocalDate> earliestDateToGive,
                    Optional<LocalDate> latestDateToGive,
                    List<String> supportingImmunizationIds,
                    boolean isDeactivated,
                    Optional<String> fulfillingImmunizationId,
                    String supportingPopulationRecommendationId,
                    String recommendedVaccinationDoseId
            )
    {
        LocalDate now = LocalDate.now();

        List<ImmunizationRecommendation.Recommendation.DateCriterion> dateCriteria = new LinkedList<>();
        if (dateToGive.isPresent()) {
            dateCriteria.add(buildImmunizationRecommendationDateCriterion(Constants.DATE_CRITERION_DATE, dateToGive.get()));
        }
        if (earliestDateToGive.isPresent()) {
            dateCriteria.add(buildImmunizationRecommendationDateCriterion(Constants.DATE_CRITERION_EARLIEST, earliestDateToGive.get()));
        }
        if (latestDateToGive.isPresent()){
            dateCriteria.add(buildImmunizationRecommendationDateCriterion(Constants.DATE_CRITERION_LATEST, latestDateToGive.get()));
        }
        List<Reference> supportingImmunizations = supportingImmunizationIds.stream()
                .map(this::buildSupportingImmunization)
                .collect(Collectors.toList());

        List<Extension> extensions = new LinkedList<>();
        extensions.add(Extension.builder()
                .url(Constants.PROFILE_BASE_URL + "vp-immunization-recommendation-is-deactivated")
                .value(isDeactivated)
                .build());
        if (fulfillingImmunizationId.isPresent()) {
            extensions.add(Extension.builder()
                    .url(Constants.PROFILE_BASE_URL + "vp-fulfilling-immunization")
                    .value(Reference.builder()
                            .reference("Immunization/" + fulfillingImmunizationId.get())
                            .build())
                    .build());
        }
        extensions.add(Extension.builder()
                .url(Constants.PROFILE_BASE_URL + "vp-supporting-population-recommendation")
                .value(Reference.builder()
                        .reference("Basic/" + supportingPopulationRecommendationId)
                        .build())
                .build());
        extensions.add(Extension.builder()
                .url(Constants.PROFILE_BASE_URL + "vp-recommended-vaccination-dose")
                .value(Reference.builder()
                        .reference("Basic/" + recommendedVaccinationDoseId)
                        .build())
                .build());


        return ImmunizationRecommendation.builder()
                .meta(Meta.builder()
                        .profile(Canonical.of(Constants.PROFILE_BASE_URL + "vp-immunization-recommendation"))
                        .build())
                .patient(Reference.builder()
                        .reference("Patient/" + patientId)
                        .build())
                .date(DateTime.of(LocalDate.of(now.getYear(), now.getMonth(), now.getDayOfMonth())))
                .recommendation(ImmunizationRecommendation.Recommendation.builder()
                        .vaccineCode(CodeableConcept.builder()
                                .coding(Coding.builder()
                                        .system(Uri.builder()
                                                .value("http://fhir.de/CodeSystem/ifa/pzn")
                                                .build())
                                        .code(Code.builder()
                                                .value(vaccineCode)
                                                .build())
                                        .build())
                                .build())
                        .targetDisease(CodeableConcept.builder()
                                .coding(Coding.builder()
                                        .system(Uri.builder()
                                                .value("http://hl7.org/fhir/sid/icd-10")
                                                .build())
                                        .code(Code.builder()
                                                .value(targetDiseaseCode)
                                                .build())
                                        .build())
                                .build())
                        .forecastStatus(CodeableConcept.builder()
                                .coding(Coding.builder()
                                        .system(Uri.builder()
                                                .value("http://terminology.hl7.org/CodeSystem/immunization-recommendation-status")
                                                .build())
                                        .code(Code.builder()
                                                .value(forecastStatus.name().toLowerCase())
                                                .build())
                                        .build())
                                .build())
                        .dateCriterion(dateCriteria)
                        .supportingImmunization(supportingImmunizations)
                        .build())
                .extension(extensions)
                .build();
    }

    private ImmunizationRecommendation.Recommendation.DateCriterion buildImmunizationRecommendationDateCriterion(String loincCode, LocalDate date) {
        return ImmunizationRecommendation.Recommendation.DateCriterion.builder()
                .code(CodeableConcept.builder()
                        .coding(Coding.builder()
                                .system(Uri.builder()
                                        .value("http://loinc.org")
                                        .build())
                                .code(Code.builder()
                                        .value(loincCode)
                                        .build())
                                .build())
                        .build())
                .value(DateTime.of(LocalDate.of(date.getYear(), date.getMonth(), date.getDayOfMonth())))
                .build();
    }

    private Reference buildSupportingImmunization(String id) {
        return Reference.builder()
                .reference("Immunization/" + id)
                .build();
    }

}
