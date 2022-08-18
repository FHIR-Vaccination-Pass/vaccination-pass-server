package org.fhirvp.model.mapper;

import com.ibm.fhir.model.resource.ImmunizationRecommendation;
import com.ibm.fhir.model.type.Reference;
import com.ibm.fhir.path.FHIRPathNode;
import io.vavr.control.Try;
import org.fhirvp.Constants;
import org.fhirvp.model.ForecastStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ImmunizationRecommendationMapped extends FHIRResourceMapped<ImmunizationRecommendation> {

    public ImmunizationRecommendationMapped(ImmunizationRecommendation immunizationRecommendation) {
        super(immunizationRecommendation);
    }

    public String getId() {
        return _raw.getId();
    }

    public String getPatientId() {
        String[] referenceParts = _raw.getPatient().getReference().getValue().split("/");
        return referenceParts[referenceParts.length - 1];
    }

    public LocalDate getCreatedDate() {
        return convertTemporalAccessorToLocalDate(_raw.getDate().as(com.ibm.fhir.model.type.DateTime.class).getValue());
    }

    public String getVaccineCode() {
        String fhirPath = "recommendation.vaccineCode.coding.where(system = 'http://fhir.de/CodeSystem/ifa/pzn').code";
        return super.createCodeValue(_raw, fhirPath);
    }

    public String getTargetDiseaseCode() {
        String fhirPath = "recommendation.targetDisease.coding.where(system = 'http://hl7.org/fhir/sid/icd-10').code";
        return super.createCodeValue(_raw, fhirPath);
    }

    public ForecastStatus getForecastStatus() {
        String fhirPath = "recommendation.forecastStatus.coding.where(system = 'http://terminology.hl7.org/CodeSystem/immunization-recommendation-status').code";
        switch (super.createCodeValue(_raw, fhirPath)) {
            case "due":
                return ForecastStatus.DUE;
            case "overdue":
                return ForecastStatus.OVERDUE;
            case "immune":
                return ForecastStatus.IMMUNE;
            case "contraindicated":
                return ForecastStatus.CONTRAINDICATED;
            case "complete":
                return ForecastStatus.COMPLETE;
            default:
                throw new IllegalArgumentException("ForecastStatusCode isn't from CodeSystem 'http://terminology.hl7.org/CodeSystem/immunization-recommendation-status'");
        }
    }

    public Optional<LocalDate> getDateToGive() {
        return getDateCriterion(Constants.DATE_CRITERION_DATE);
    }

    public Optional<LocalDate> getEarliestDateToGive() {
        return getDateCriterion(Constants.DATE_CRITERION_EARLIEST);
    }

    public Optional<LocalDate> getLatestDateToGive() {
        return getDateCriterion(Constants.DATE_CRITERION_LATEST);
    }

    private Optional<LocalDate> getDateCriterion(String loincCode) {
        String fhirPath = "recommendation.dateCriterion.where(code.coding.where(system = 'http://loinc.org').code = '" + loincCode + "').value";
        Optional<FHIRPathNode> optionalNode = super.createOptionalOne(_raw, fhirPath);
        if (optionalNode.isEmpty()) {
            return Optional.empty();
        }
        com.ibm.fhir.model.type.DateTime dateTime = optionalNode.get().asElementNode().element()
                .as(com.ibm.fhir.model.type.DateTime.class);
        return Optional.of(convertTemporalAccessorToLocalDate(dateTime.getValue()));
    }

    public List<String> getSupportingImmunizationIds() {
        String fhirPath = "recommendation.supportingImmunization";
        return Try.of(() -> fhirPathEvaluator.evaluate(_raw, fhirPath)).get().stream()
                .map(fhirPathNode -> fhirPathNode.asElementNode().element().as(Reference.class)
                        .getReference().getValue().split("/"))
                .map(referenceParts -> referenceParts[referenceParts.length - 1])
                .collect(Collectors.toList());
    }

    public boolean isDeactivated() {
        String fhirPath = "extension.where(url = '" + Constants.PROFILE_BASE_URL + "vp-immunization-recommendation-is-deactivated').value";
        return super.createExactlyOne(_raw, fhirPath).as(com.ibm.fhir.model.type.Boolean.class).getValue();
    }

    public Optional<String> getFulfillingImmunizationId() {
        String fhirPath = "extension.where(url = '" + Constants.PROFILE_BASE_URL + "vp-fulfilling-immunization').value";
        Optional<FHIRPathNode> optionalNode = super.createOptionalOne(_raw, fhirPath);
        if (optionalNode.isEmpty()) {
            return Optional.empty();
        }
        String[] referenceParts = optionalNode.get().asElementNode().element()
                .as(Reference.class).getReference().getValue().split("/");
        return Optional.of(referenceParts[referenceParts.length - 1]);
    }

    public String getSupportingPopulationRecommendationId() {
        String fhirPath = "extension.where(url = '" + Constants.PROFILE_BASE_URL + "vp-supporting-population-recommendation').value";
        return createNonOptionalReferenceId(fhirPath);
    }

    public String getRecommendedVaccinationDoseId() {
        String fhirPath = "extension.where(url = '" + Constants.PROFILE_BASE_URL + "vp-recommended-vaccination-dose').value";
        return createNonOptionalReferenceId(fhirPath);
    }

    private String createNonOptionalReferenceId(String fhirPath) {
        String[] referenceParts = super.createExactlyOne(_raw, fhirPath)
                .as(Reference.class).getReference().getValue().split("/");
        return referenceParts[referenceParts.length - 1];
    }

}
