package org.fhirvp.usecase.impl;

import com.ibm.fhir.model.resource.*;
import com.ibm.fhir.model.type.*;
import com.ibm.fhir.model.type.code.AddressUse;
import com.ibm.fhir.model.type.code.AdministrativeGender;
import com.ibm.fhir.model.type.code.ImmunizationStatus;
import com.ibm.fhir.model.type.code.NameUse;
import io.vavr.control.Try;
import lombok.Builder;
import lombok.Value;
import org.fhirvp.ports.*;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;
import org.fhirvp.usecase.BootstrapUseCase;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.lang.String;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Builder
@Value
class CreateMedicationResult {
    Medication medication;
    List<Basic> vaccinationDoses;
    List<Basic> vaccinationSchemes;
}

@ApplicationScoped
public class BootstrapUseCaseImpl implements BootstrapUseCase {
    static final String PROFILE_BASE_URL = "https://fhir-vaccination-pass.github.io/fhir-implementation-guide/StructureDefinition/";

    @Inject
    BasicPort basicPort;
    @Inject
    ImmunizationPort immunizationPort;
    @Inject
    ImmunizationRecommendationPort immunizationRecommendationPort;
    @Inject
    MedicationPort medicationPort;
    @Inject
    OrganizationPort organizationPort;
    @Inject
    PatientPort patientPort;
    @Inject
    PractitionerPort practitionerPort;

    private Basic createActiveVaccinationScheme(Patient patient, Basic vaccinationScheme) throws FHIRServerException {
        return basicPort.createAndRead(Basic.builder()
                .meta(Meta.builder()
                        .profile(Canonical.of(PROFILE_BASE_URL + "vp-active-vaccination-scheme"))
                        .build())
                .code(CodeableConcept.builder()
                        .coding(Coding.builder()
                                .code(Code.builder()
                                        .value("ActiveVaccinationScheme")
                                        .build())
                                .build())
                        .build())
                .extension(Extension.builder()
                        .url(PROFILE_BASE_URL + "vp-active-vaccination-scheme-extension")
                        .extension(
                                Extension.builder()
                                        .url("vaccinationScheme")
                                        .value(Reference.builder()
                                                .reference("Basic/" + vaccinationScheme.getId())
                                                .build())
                                        .build(),
                                Extension.builder()
                                        .url("patient")
                                        .value(Reference.builder()
                                                .reference("Patient/" + patient.getId())
                                                .build())
                                        .build(),
                                Extension.builder()
                                        .url("changeReason")
                                        .value(Markdown.of("This is a reason."))
                                        .build()
                        )
                        .build())
                .build());
    }

    private Immunization createImmunization(
            Patient patient,
            Practitioner performer,
            Medication medication,
            Basic vaccinationDose,
            String lotNumber,
            DateTime occurence
    ) throws FHIRServerException {
        return immunizationPort.createAndRead(Immunization.builder()
                .meta(Meta.builder().profile(Canonical.of(PROFILE_BASE_URL + "vp-immunization")).build())
                .status(ImmunizationStatus.COMPLETED)
                .vaccineCode(medication.getCode())
                .patient(Reference.builder().reference("Patient/" + patient.getId()).build())
                .occurrence(occurence)
                .lotNumber(lotNumber)
                .performer(Immunization.Performer.builder()
                        .actor(Reference.builder().reference("Practitioner/" + performer.getId()).build())
                        .build())
                .extension(Extension.builder()
                        .url(PROFILE_BASE_URL + "vp-administered-vaccination-dose")
                        .value(Reference.builder().reference("Basic/" + vaccinationDose.getId()).build())
                        .build())
                .build());
    }

    private ImmunizationRecommendation createImmunizationRecommendation(
            Patient patient,
            Code vaccineCode,
            Code targetDisease,
            Code forecastStatus,
            Basic populationRecommendation,
            DateTime date,
            DateTime earliestDateToGive,
            DateTime latestDateToGive,
            List<Immunization> supportingImmunizations,
            Basic recommendedVaccinationDose

    ) throws FHIRServerException {
        return immunizationRecommendationPort.createAndRead(ImmunizationRecommendation.builder()
                .meta(Meta.builder()
                        .profile(Canonical.of(PROFILE_BASE_URL + "vp-immunization-recommendation"))
                        .build())
                .patient(Reference.builder()
                        .reference("Patient/" + patient.getId())
                        .build())
                .date(date)
                .recommendation(ImmunizationRecommendation.Recommendation.builder()
                        .vaccineCode(CodeableConcept.builder()
                                .coding(Coding.builder()
                                        .system(Uri.builder()
                                                .value("http://fhir.de/CodeSystem/ifa/pzn")
                                                .build())
                                        .code(vaccineCode)
                                        .build())
                                .build())
                        .targetDisease(CodeableConcept.builder()
                                .coding(Coding.builder()
                                        .system(Uri.builder()
                                                .value("http://hl7.org/fhir/sid/icd-10")
                                                .build())
                                        .code(targetDisease)
                                        .build())
                                .build())
                        .forecastStatus(CodeableConcept.builder()
                                .coding(Coding.builder()
                                        .system(Uri.builder()
                                                .value("http://terminology.hl7.org/CodeSystem/immunization-recommendation-status")
                                                .build())
                                        .code(forecastStatus)
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
                                        .value(earliestDateToGive)
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
                                        .value(latestDateToGive)
                                        .build()
                        )
                        .supportingImmunization(supportingImmunizations.stream().map(
                                immunization -> Reference.builder().reference("Immunization/" + immunization.getId()).build()
                        ).collect(Collectors.toList()))
                        .build())
                .extension(Extension.builder()
                        .url(PROFILE_BASE_URL + "vp-immunization-recommendation-is-deactivated")
                        .value(false)
                        .build())
                .extension(Extension.builder()
                        .url(PROFILE_BASE_URL + "vp-supporting-population-recommendation")
                        .value(Reference.builder()
                                .reference("Basic/" + populationRecommendation.getId())
                                .build())
                        .build())
                .extension(Extension.builder()
                        .url(PROFILE_BASE_URL + "vp-recommended-vaccination-dose")
                        .value(Reference.builder()
                                .reference("Basic/" + recommendedVaccinationDose.getId())
                                .build())
                        .build())
                .build());
    }

    private CreateMedicationResult createMedicationComirnaty(Organization organization) throws FHIRServerException {
        var medication = medicationPort.createAndRead(Medication.builder()
                .meta(Meta.builder()
                        .profile(Canonical.of(PROFILE_BASE_URL + "vp-medication"))
                        .build())
                .code(CodeableConcept.builder()
                        .coding(Coding.builder()
                                .system(Uri.builder()
                                        .value("http://fhir.de/CodeSystem/ifa/pzn")
                                        .build())
                                .code(Code.builder()
                                        .value("17377588")
                                        .build())
                                .build())
                        .build())
                .manufacturer(Reference.builder()
                        .reference("Organization/" + organization.getId())
                        .build())
                .form(CodeableConcept.builder()
                        .coding(Coding.builder()
                                .system(Uri.builder()
                                        .value("http://snomed.info/sct")
                                        .build())
                                .code(Code.builder()
                                        .value("736542009")
                                        .build())
                                .build())
                        .build())
                .extension(Extension.builder()
                        .url(PROFILE_BASE_URL + "vp-medication-trade-name")
                        .value("Comirnaty")
                        .build())
                .extension(
                        Extension.builder()
                                .url(PROFILE_BASE_URL + "vp-medication-target-disease")
                                .value(CodeableConcept.builder()
                                        .coding(Coding.builder()
                                                .system(Uri.builder()
                                                        .value("http://hl7.org/fhir/sid/icd-10")
                                                        .build())
                                                .code(Code.builder()
                                                        .value("U07.1")
                                                        .build())
                                                .build())
                                        .build())
                                .build()
                )
                .build());
        var vaccinationSchemeStandard = basicPort.createAndRead(Basic.builder()
                .meta(Meta.builder()
                        .profile(Canonical.of(PROFILE_BASE_URL + "vp-vaccination-scheme"))
                        .build())
                .code(CodeableConcept.builder()
                        .coding(Coding.builder()
                                .code(Code.builder()
                                        .value("VaccinationScheme")
                                        .build())
                                .build())
                        .build())
                .extension(Extension.builder()
                        .url(PROFILE_BASE_URL + "vp-vaccination-scheme-extension")
                        .extension(
                                Extension.builder()
                                        .url("name")
                                        .value("Standard")
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
                                        .url("vaccine")
                                        .value(Reference.builder()
                                                .reference("Medication/" + medication.getId())
                                                .build())
                                        .build()
                        )
                        .build())
                .build());
        var vaccinationSchemeStandardDoses = Try.traverse(
                List.of(
                        Basic.builder()
                                .meta(Meta.builder()
                                        .profile(Canonical.of(PROFILE_BASE_URL + "vp-vaccination-dose"))
                                        .build())
                                .code(CodeableConcept.builder()
                                        .coding(Coding.builder()
                                                .code(Code.builder()
                                                        .value("VaccinationDose")
                                                        .build())
                                                .build())
                                        .build())
                                .extension(Extension.builder()
                                        .url(PROFILE_BASE_URL + "vp-vaccination-dose-base-extension")
                                        .extension(
                                                Extension.builder()
                                                        .url("doseQuantity")
                                                        .value(Quantity.builder()
                                                                .code(Code.of("ml"))
                                                                .system(Uri.of("http://unitsofmeasure.org"))
                                                                .value(Decimal.of(0.3))
                                                                .build())
                                                        .build(),
                                                Extension.builder()
                                                        .url("isProtected")
                                                        .value(false)
                                                        .build(),
                                                Extension.builder()
                                                        .url("notes")
                                                        .value(Markdown.of("This is a note."))
                                                        .build(),
                                                Extension.builder()
                                                        .url("vaccinationScheme")
                                                        .value(Reference.builder()
                                                                .reference("Basic/" + vaccinationSchemeStandard.getId())
                                                                .build())
                                                        .build()
                                        )
                                        .build())
                                .extension(Extension.builder()
                                        .url(PROFILE_BASE_URL + "vp-vaccination-dose-single-extension")
                                        .extension(
                                                Extension.builder()
                                                        .url("numberInScheme")
                                                        .value(UnsignedInt.of(1))
                                                        .build()
                                        )
                                        .build())
                                .build(),
                        Basic.builder()
                                .meta(Meta.builder()
                                        .profile(Canonical.of(PROFILE_BASE_URL + "vp-vaccination-dose"))
                                        .build())
                                .code(CodeableConcept.builder()
                                        .coding(Coding.builder()
                                                .code(Code.builder()
                                                        .value("VaccinationDose")
                                                        .build())
                                                .build())
                                        .build())
                                .extension(Extension.builder()
                                        .url(PROFILE_BASE_URL + "vp-vaccination-dose-base-extension")
                                        .extension(
                                                Extension.builder()
                                                        .url("doseQuantity")
                                                        .value(Quantity.builder()
                                                                .code(Code.of("ml"))
                                                                .system(Uri.of("http://unitsofmeasure.org"))
                                                                .value(Decimal.of(0.3))
                                                                .build())
                                                        .build(),
                                                Extension.builder()
                                                        .url("isProtected")
                                                        .value(true)
                                                        .build(),
                                                Extension.builder()
                                                        .url("notes")
                                                        .value(Markdown.of("This is a note."))
                                                        .build(),
                                                Extension.builder()
                                                        .url("vaccinationScheme")
                                                        .value(Reference.builder()
                                                                .reference("Basic/" + vaccinationSchemeStandard.getId())
                                                                .build())
                                                        .build()
                                        )
                                        .build())
                                .extension(Extension.builder()
                                        .url(PROFILE_BASE_URL + "vp-vaccination-dose-single-extension")
                                        .extension(
                                                Extension.builder()
                                                        .url("numberInScheme")
                                                        .value(UnsignedInt.of(2))
                                                        .build(),
                                                Extension.builder()
                                                        .url("timeframeStart")
                                                        .value(Quantity.builder()
                                                                .code(Code.of("wk"))
                                                                .system(Uri.of("http://unitsofmeasure.org"))
                                                                .value(Decimal.of(3))
                                                                .build())
                                                        .build(),
                                                Extension.builder()
                                                        .url("timeframeEnd")
                                                        .value(Quantity.builder()
                                                                .code(Code.of("wk"))
                                                                .system(Uri.of("http://unitsofmeasure.org"))
                                                                .value(Decimal.of(5))
                                                                .build())
                                                        .build()
                                        )
                                        .build())
                                .build(),
                        Basic.builder()
                                .meta(Meta.builder()
                                        .profile(Canonical.of(PROFILE_BASE_URL + "vp-vaccination-dose"))
                                        .build())
                                .code(CodeableConcept.builder()
                                        .coding(Coding.builder()
                                                .code(Code.builder()
                                                        .value("VaccinationDose")
                                                        .build())
                                                .build())
                                        .build())
                                .extension(Extension.builder()
                                        .url(PROFILE_BASE_URL + "vp-vaccination-dose-base-extension")
                                        .extension(
                                                Extension.builder()
                                                        .url("doseQuantity")
                                                        .value(Quantity.builder()
                                                                .code(Code.of("ml"))
                                                                .system(Uri.of("http://unitsofmeasure.org"))
                                                                .value(Decimal.of(0.3))
                                                                .build())
                                                        .build(),
                                                Extension.builder()
                                                        .url("isProtected")
                                                        .value(true)
                                                        .build(),
                                                Extension.builder()
                                                        .url("notes")
                                                        .value(Markdown.of("This is a note."))
                                                        .build(),
                                                Extension.builder()
                                                        .url("vaccinationScheme")
                                                        .value(Reference.builder()
                                                                .reference("Basic/" + vaccinationSchemeStandard.getId())
                                                                .build())
                                                        .build()
                                        )
                                        .build())
                                .extension(Extension.builder()
                                        .url(PROFILE_BASE_URL + "vp-vaccination-dose-repeating-extension")
                                        .extension(
                                                Extension.builder()
                                                        .url("numberInScheme")
                                                        .value(UnsignedInt.of(3))
                                                        .build(),
                                                Extension.builder()
                                                        .url("interval")
                                                        .value(Quantity.builder()
                                                                .code(Code.of("mo"))
                                                                .system(Uri.of("http://unitsofmeasure.org"))
                                                                .value(Decimal.of(6))
                                                                .build())
                                                        .build()
                                        )
                                        .build())
                                .build()
                ),
                basicPort::tryCreateAndRead
        ).get().asJava();

        return CreateMedicationResult.builder()
                .medication(medication)
                .vaccinationDoses(vaccinationSchemeStandardDoses)
                .vaccinationSchemes(Stream.of(
                        vaccinationSchemeStandardDoses.stream()
                ).flatMap(Function.identity()).collect(Collectors.toList()))
                .build();
    }

    private Organization createOrganization(String name) throws FHIRServerException {
        return organizationPort.createAndRead(
                Organization.builder()
                        .meta(Meta.builder()
                                .profile(Canonical.of(PROFILE_BASE_URL + "vp-organization"))
                                .build())
                        .name(name)
                        .build()
        );
    }

    private Patient createPatient(
            String givenName,
            String familyName,
            Date birthDate,
            Code countryCode,
            Code stateCode,
            String keyCloakUsername,
            boolean isPregnant
    ) throws FHIRServerException {
        return patientPort.createAndRead(Patient.builder()
                .meta(Meta.builder()
                        .profile(Canonical.of(PROFILE_BASE_URL + "vp-patient"))
                        .build())
                .active(true)
                .name(
                        HumanName.builder()
                                .use(NameUse.OFFICIAL)
                                .family(familyName)
                                .given(givenName)
                                .build()
                )
                .gender(AdministrativeGender.MALE)
                .birthDate(birthDate)
                .deceased(false)
                .address(Address.builder()
                        .use(AddressUse.HOME)
                        .country(com.ibm.fhir.model.type.String.builder()
                                .value("Germany")
                                .extension(Extension.builder()
                                        .url(PROFILE_BASE_URL + "vp-country-code-extension")
                                        .value(CodeableConcept.builder()
                                                .coding(Coding.builder()
                                                        .system(Uri.of("urn:iso:std:iso:3166"))
                                                        .code(countryCode)
                                                        .build())
                                                .build())
                                        .build())
                                .build())
                        .state(com.ibm.fhir.model.type.String.builder()
                                .value("Bavaria")
                                .extension(Extension.builder()
                                        .url(PROFILE_BASE_URL + "vp-state-code-extension")
                                        .value(CodeableConcept.builder()
                                                .coding(Coding.builder()
                                                        .system(Uri.of("urn:iso:std:iso:3166:-2"))
                                                        .code(stateCode)
                                                        .build())
                                                .build())
                                        .build())
                                .build())
                        .build())
                .extension(Extension.builder()
                        .url(PROFILE_BASE_URL + "vp-patient-keycloak-username-extension")
                        .value(keyCloakUsername)
                        .build())
                .extension(Extension.builder()
                        .url(PROFILE_BASE_URL + "vp-patient-is-pregnant-extension")
                        .value(isPregnant)
                        .build())
                .build());
    }

    private List<Basic> createPopulationRecommendations() throws FHIRServerException {
        var populationRecommendationsToCreate = List.of(
                Basic.builder()
                        .meta(Meta.builder()
                                .profile(Canonical.of(PROFILE_BASE_URL + "vp-population-recommendation"))
                                .build())
                        .code(CodeableConcept.builder()
                                .coding(Coding.builder()
                                        .code(Code.builder()
                                                .value("PopulationRecommendation")
                                                .build())
                                        .build())
                                .build())
                        .extension(Extension.builder()
                                .url(PROFILE_BASE_URL + "vp-population-recommendation-extension")
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
                                                .build(),
                                        Extension.builder()
                                                .url(PROFILE_BASE_URL + "vp-location-extension")
                                                .extension(
                                                        Extension.builder()
                                                                .url(PROFILE_BASE_URL + "vp-country-code-extension")
                                                                .value(CodeableConcept.builder()
                                                                        .coding(Coding.builder()
                                                                                .system(Uri.of("urn:iso:std:iso:3166"))
                                                                                .code(Code.of("DE"))
                                                                                .build())
                                                                        .build())
                                                                .build(),
                                                        Extension.builder()
                                                                .url(PROFILE_BASE_URL + "vp-state-code-extension")
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
                        .build()
        );
        return Try.traverse(populationRecommendationsToCreate, basicPort::tryCreateAndRead).get().asJava();
    }

    private Practitioner createPractitioner(String givenName, String familyName) throws FHIRServerException {
        return practitionerPort.createAndRead(Practitioner.builder()
                .meta(Meta.builder()
                        .profile(Canonical.of(PROFILE_BASE_URL + "vp-practitioner"))
                        .build())
                .name(HumanName.builder()
                        .use(NameUse.OFFICIAL)
                        .family(familyName)
                        .given(givenName)
                        .build())
                .build());
    }

    private List<Basic> createTargetDiseases() throws FHIRServerException {
        var targetDiseasesToCreate = List.of(
                Basic.builder()
                        .meta(Meta.builder()
                                .profile(Canonical.of(PROFILE_BASE_URL + "vp-target-disease"))
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
                        .build()
        );
        return Try.traverse(targetDiseasesToCreate, basicPort::tryCreateAndRead).get().asJava();
    }

    private List<Basic> createVacationPlans() throws FHIRServerException {
        var vacationPlansToCreate = List.of(
                Basic.builder()
                        .meta(Meta.builder()
                                .profile(Canonical.of(PROFILE_BASE_URL + "vp-vacation-plan"))
                                .build())
                        .code(CodeableConcept.builder()
                                .coding(Coding.builder()
                                        .code(Code.builder()
                                                .value("VacationPlan")
                                                .build())
                                        .build())
                                .build())
                        .extension(Extension.builder()
                                .url(PROFILE_BASE_URL + "vp-vacation-plan-extension")
                                .extension(Extension.builder()
                                        .url(PROFILE_BASE_URL + "vp-location-extension")
                                        .extension(Extension.builder()
                                                .url(PROFILE_BASE_URL + "vp-country-code-extension")
                                                .value(CodeableConcept.builder()
                                                        .coding(Coding.builder()
                                                                .system(Uri.of("urn:iso:std:iso:3166"))
                                                                .code(Code.of("DE"))
                                                                .build())
                                                        .build())
                                                .build())
                                        .extension(Extension.builder()
                                                .url(PROFILE_BASE_URL + "vp-state-code-extension")
                                                .value(CodeableConcept.builder()
                                                        .coding(Coding.builder()
                                                                .system(Uri.of("urn:iso:std:iso:3166:-2"))
                                                                .code(Code.of("DE-BY"))
                                                                .build())
                                                        .build())
                                                .build())
                                        .build())
                                .extension(
                                        Extension.builder()
                                                .url("departureDate")
                                                .value(Date.of("2000-01-01"))
                                                .build(),
                                        Extension.builder()
                                                .url("patient")
                                                .value(Reference.builder()
                                                        .reference("https://example.com/Patient/123")
                                                        .build())
                                                .build()
                                )
                                .build())
                        .build()
        );
        return Try.traverse(vacationPlansToCreate, basicPort::tryCreateAndRead).get().asJava();
    }

    @Override
    public BootstrapResult bootstrap() throws FHIRServerException {
        var result = BootstrapResult.builder()
                .activeVaccinationSchemes(new ArrayList<>())
                .immunizations(new ArrayList<>())
                .immunizationRecommendations(new ArrayList<>())
                .medications(new ArrayList<>())
                .organizations(new ArrayList<>())
                .patients(new ArrayList<>())
                .populationRecommendations(new ArrayList<>())
                .practitioners(new ArrayList<>())
                .targetDiseases(new ArrayList<>())
                .vacationPlans(new ArrayList<>())
                .vaccinationDoses(new ArrayList<>())
                .vaccinationSchemes(new ArrayList<>())
                .build();

        var populationRecommendations = createPopulationRecommendations();
        result.getPopulationRecommendations().addAll(populationRecommendations);

        var targetDiseases = createTargetDiseases();
        result.getTargetDiseases().addAll(targetDiseases);

        var vacationPlans = createVacationPlans();
        result.getVacationPlans().addAll(vacationPlans);

        var organizationBioNTech = createOrganization("BioNTech Manufacturing GmbH");
        result.getOrganizations().add(organizationBioNTech);

        var medicationComirnatyResult = createMedicationComirnaty(organizationBioNTech);
        result.getMedications().add(medicationComirnatyResult.getMedication());
        result.getVaccinationDoses().addAll(medicationComirnatyResult.getVaccinationDoses());
        result.getVaccinationSchemes().addAll(medicationComirnatyResult.getVaccinationSchemes());

        var practitionerDrHouse = createPractitioner("Gregory", "House");
        result.getPractitioners().add(practitionerDrHouse);

        // patient who is fully covered
        {
            var patient = createPatient(
                    "Hans",
                    "Müller",
                    Date.of(LocalDate.of(2000, 1, 1)),
                    Code.of("DE"),
                    Code.of("DE-BY"),
                    "hans.müller",
                    false
            );
            result.getPatients().add(patient);

            var immunizations = List.of(
                    createImmunization(
                            patient,
                            practitionerDrHouse,
                            medicationComirnatyResult.getMedication(),
                            medicationComirnatyResult.getVaccinationDoses().get(0),
                            "COMIRNATY-001",
                            DateTime.of(LocalDate.of(2020, 1, 1))
                    ),
                    createImmunization(
                            patient,
                            practitionerDrHouse,
                            medicationComirnatyResult.getMedication(),
                            medicationComirnatyResult.getVaccinationDoses().get(1),
                            "COMIRNATY-002",
                            DateTime.of(LocalDate.of(2020, 2, 1))
                    ),
                    createImmunization(
                            patient,
                            practitionerDrHouse,
                            medicationComirnatyResult.getMedication(),
                            medicationComirnatyResult.getVaccinationDoses().get(2),
                            "COMIRNATY-003",
                            DateTime.of(LocalDate.of(2020, 8, 1))
                    )
            );
            result.getImmunizations().addAll(immunizations);

            var activeVaccinationScheme = createActiveVaccinationScheme(
                    patient,
                    medicationComirnatyResult.getVaccinationSchemes().get(0)
            );
            result.getActiveVaccinationSchemes().add(activeVaccinationScheme);
        }

        return result;
    }
}
