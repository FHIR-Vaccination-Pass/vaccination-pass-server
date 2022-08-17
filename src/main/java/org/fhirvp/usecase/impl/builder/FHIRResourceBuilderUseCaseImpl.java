package org.fhirvp.usecase.impl.builder;

import com.ibm.fhir.model.resource.Basic;
import com.ibm.fhir.model.resource.ImmunizationRecommendation;
import com.ibm.fhir.model.type.*;
import org.fhirvp.Constants;
import org.fhirvp.model.ForecastStatus;
import org.fhirvp.ports.impl.fhir.context.FHIRClientConfig;
import org.fhirvp.usecase.FHIRResourceBuilderUseCase;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.lang.String;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class FHIRResourceBuilderUseCaseImpl implements FHIRResourceBuilderUseCase {

    @Inject
    FHIRClientConfig fhirClientConfig;

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
                        .reference(fhirClientConfig.rest().base().url() + "/Patient/" + patientId)
                        .build())
                .extension(Extension.builder()
                        .url(Constants.PROFILE_BASE_URL + "vp-active-vaccination-scheme-extension")
                        .extension(
                                Extension.builder()
                                        .url("vaccinationScheme")
                                        .value(Reference.builder()
                                                .reference(fhirClientConfig.rest().base().url() + "/Basic/" + vaccinationSchemeId)
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
                    String patientId, String vaccineCode, String targetDiseaseCode, ForecastStatus forecastStatus,
                    Optional<Calendar> dateToGive, Optional<Calendar> earliestDateToGive, Optional<Calendar> latestDateToGive,
                    List<String> supportingImmunizationIds, boolean isDeactivated, Optional<String> fulfillingImmunizationId,
                    String supportingPopulationRecommendationId, String recommendedVaccinationDoseId
            ) {
        Calendar now = Calendar.getInstance();

        List<ImmunizationRecommendation.Recommendation.DateCriterion> dateCriteria = new LinkedList<>();
        if (dateToGive.isPresent()) {
            dateCriteria.add(buildImmunizationRecommendationDateCriterion(Constants.DATE_CRITERION_DATE, dateToGive.get()));
        } else if (earliestDateToGive.isPresent() && latestDateToGive.isPresent()) {
            dateCriteria.add(buildImmunizationRecommendationDateCriterion(Constants.DATE_CRITERION_EARLIEST, earliestDateToGive.get()));
            dateCriteria.add(buildImmunizationRecommendationDateCriterion(Constants.DATE_CRITERION_LATEST, latestDateToGive.get()));
        } else {
            throw new IllegalArgumentException("Either dateToGive or earliestDateToGive and latestDateToGive need to be present");
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
                            .reference(fhirClientConfig.rest().base().url() + "/Immunization/" + fulfillingImmunizationId.get())
                            .build())
                    .build());
        }
        extensions.add(Extension.builder()
                .url(Constants.PROFILE_BASE_URL + "vp-supporting-population-recommendation")
                .value(Reference.builder()
                        .reference(fhirClientConfig.rest().base().url() + "/Basic/" + supportingPopulationRecommendationId)
                        .build())
                .build());
        extensions.add(Extension.builder()
                .url(Constants.PROFILE_BASE_URL + "vp-recommended-vaccination-dose")
                .value(Reference.builder()
                        .reference(fhirClientConfig.rest().base().url() + "/Basic/" + recommendedVaccinationDoseId)
                        .build())
                .build());


        return ImmunizationRecommendation.builder()
                .meta(Meta.builder()
                        .profile(Canonical.of(Constants.PROFILE_BASE_URL + "vp-immunization-recommendation"))
                        .build())
                .patient(Reference.builder()
                        .reference(fhirClientConfig.rest().base().url() + "/Patient/" + patientId)
                        .build())
                .date(DateTime.of(LocalDate.of(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))))
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

    private ImmunizationRecommendation.Recommendation.DateCriterion buildImmunizationRecommendationDateCriterion(String loincCode, Calendar date) {
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
                .value(DateTime.of(LocalDate.of(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH))))
                .build();
    }

    private Reference buildSupportingImmunization(String id) {
        return Reference.builder()
                .reference(fhirClientConfig.rest().base().url() + "/Immunization/")
                .build();
    }

}
