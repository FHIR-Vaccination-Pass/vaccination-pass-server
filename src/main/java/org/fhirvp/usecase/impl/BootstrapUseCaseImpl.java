package org.fhirvp.usecase.impl;

import com.ibm.fhir.model.resource.*;
import com.ibm.fhir.model.type.*;
import com.ibm.fhir.model.type.code.AddressUse;
import com.ibm.fhir.model.type.code.AdministrativeGender;
import com.ibm.fhir.model.type.code.ImmunizationStatus;
import com.ibm.fhir.model.type.code.NameUse;
import io.vavr.control.Try;
import org.fhirvp.ports.*;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;
import org.fhirvp.usecase.BootstrapUseCase;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.lang.String;
import java.time.LocalDate;
import java.util.List;

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

    private List<Basic> createActiveVaccinationSchemes() throws FHIRServerException {
        var activeVaccinationSchemesToCreate = List.of(
                Basic.builder()
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
                                                        .reference("https://example.com/Basic/123")
                                                        .build())
                                                .build(),
                                        Extension.builder()
                                                .url("patient")
                                                .value(Reference.builder()
                                                        .reference("https://example.com/Patient/123")
                                                        .build())
                                                .build(),
                                        Extension.builder()
                                                .url("changeReason")
                                                .value(Markdown.of("This is a reason."))
                                                .build()
                                )
                                .build())
                        .build()
        );
        return Try.traverse(activeVaccinationSchemesToCreate, basicPort::tryCreateAndRead).get().asJava();
    }

    private List<Immunization> createImmunizations() throws FHIRServerException {
        var immunizationsToCreate = List.of(
                Immunization.builder()
                        .meta(Meta.builder()
                                .profile(Canonical.of(PROFILE_BASE_URL + "vp-immunization"))
                                .build())
                        .status(ImmunizationStatus.COMPLETED)
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
                        .patient(Reference.builder()
                                .reference("https://example.com/Patient/123")
                                .build())
                        .occurrence(DateTime.of(LocalDate.of(2022, 7, 10)))
                        .lotNumber("lotNumber-abc")
                        .performer(Immunization.Performer.builder()
                                .actor(Reference.builder()
                                        .reference("https://example.com/Practitioner/456")
                                        .build())
                                .build())
                        .extension(Extension.builder()
                                .url(PROFILE_BASE_URL + "vp-administered-vaccination-dose")
                                .value(Reference.builder()
                                        .reference("https://example.com/Basic/789")
                                        .build())
                                .build())
                        .build()
        );
        return Try.traverse(immunizationsToCreate, immunizationPort::tryCreateAndRead).get().asJava();
    }

    private List<ImmunizationRecommendation> createImmunizationRecommendations() throws FHIRServerException {
        var immunizationRecommendationsToCreate = List.of(
                ImmunizationRecommendation.builder()
                        .meta(Meta.builder()
                                .profile(Canonical.of(PROFILE_BASE_URL + "vp-immunization-recommendation"))
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
                                        .reference("https://example.com/Immunization/123")
                                        .build())
                                .build())
                        .extension(Extension.builder()
                                .url(PROFILE_BASE_URL + "vp-immunization-recommendation-is-deactivated")
                                .value(false)
                                .build())
                        .extension(Extension.builder()
                                .url(PROFILE_BASE_URL + "vp-fulfilling-immunization")
                                .value(Reference.builder()
                                        .reference("https://example.com/Immunization/456")
                                        .build())
                                .build())
                        .extension(Extension.builder()
                                .url(PROFILE_BASE_URL + "vp-supporting-population-recommendation")
                                .value(Reference.builder()
                                        .reference("https://example.com/Basic/123")
                                        .build())
                                .build())
                        .extension(Extension.builder()
                                .url(PROFILE_BASE_URL + "vp-recommended-vaccination-dose")
                                .value(Reference.builder()
                                        .reference("https://example.com/Basic/456")
                                        .build())
                                .build())
                        .build()
        );
        return Try.traverse(immunizationRecommendationsToCreate, immunizationRecommendationPort::tryCreateAndRead).get().asJava();
    }

    private List<Medication> createMedications() throws FHIRServerException {
        var medicationsToCreate = List.of(
                Medication.builder()
                        .meta(Meta.builder()
                                .profile(Canonical.of(PROFILE_BASE_URL + "vp-medication"))
                                .build())
                        .code(CodeableConcept.builder()
                                .coding(Coding.builder()
                                        .system(Uri.builder()
                                                .value("http://fhir.de/CodeSystem/ifa/pzn")
                                                .build())
                                        .code(Code.builder()
                                                .value("vaccine-abc")
                                                .build())
                                        .build())
                                .build())
                        .manufacturer(Reference.builder()
                                .reference("https://example.com/Organization/123")
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
                                .value("vaccine-name-abc")
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
                                                                .value("A00")
                                                                .build())
                                                        .build())
                                                .build())
                                        .build(),
                                Extension.builder()
                                        .url(PROFILE_BASE_URL + "vp-medication-target-disease")
                                        .value(CodeableConcept.builder()
                                                .coding(Coding.builder()
                                                        .system(Uri.builder()
                                                                .value("http://hl7.org/fhir/sid/icd-10")
                                                                .build())
                                                        .code(Code.builder()
                                                                .value("A01")
                                                                .build())
                                                        .build())
                                                .build())
                                        .build()
                        )
                        .build()
        );
        return Try.traverse(medicationsToCreate, medicationPort::tryCreateAndRead).get().asJava();
    }

    private List<Organization> createOrganizations() throws FHIRServerException {
        var organizationsToCreate = List.of(
                Organization.builder()
                        .meta(Meta.builder()
                                .profile(Canonical.of(PROFILE_BASE_URL + "vp-organization"))
                                .build())
                        .name("organization-name-abc")
                        .build()
        );
        return Try.traverse(organizationsToCreate, organizationPort::tryCreateAndRead).get().asJava();
    }

    private List<Patient> createPatients() throws FHIRServerException {
        var patientsToCreate = List.of(
                Patient.builder()
                        .meta(Meta.builder()
                                .profile(Canonical.of(PROFILE_BASE_URL + "vp-patient"))
                                .build())
                        .active(true)
                        .name(
                                HumanName.builder()
                                        .use(NameUse.OFFICIAL)
                                        .family("Müller")
                                        .given("Hubert", "Sebastian")
                                        .build(),
                                HumanName.builder()
                                        .use(NameUse.NICKNAME)
                                        .given("Hubsi")
                                        .build()
                        )
                        .gender(AdministrativeGender.MALE)
                        .birthDate(Date.builder()
                                .value("2000-01-01")
                                .build())
                        .deceased(false)
                        .address(Address.builder()
                                .use(AddressUse.HOME)
                                .state(com.ibm.fhir.model.type.String.builder()
                                        .value("Bavaria")
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
                                .country(com.ibm.fhir.model.type.String.builder()
                                        .value("Germany")
                                        .extension(Extension.builder()
                                                .url(PROFILE_BASE_URL + "vp-country-code-extension")
                                                .value(CodeableConcept.builder()
                                                        .coding(Coding.builder()
                                                                .system(Uri.of("urn:iso:std:iso:3166"))
                                                                .code(Code.of("DE"))
                                                                .build())
                                                        .build())
                                                .build())
                                        .build())
                                .build())
                        .extension(Extension.builder()
                                .url(PROFILE_BASE_URL + "vp-patient-keycloak-username-extension")
                                .value("müller")
                                .build())
                        .extension(Extension.builder()
                                .url(PROFILE_BASE_URL + "vp-patient-is-pregnant-extension")
                                .value(false)
                                .build())
                        .build()
        );
        return Try.traverse(patientsToCreate, patientPort::tryCreateAndRead).get().asJava();
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

    private List<Practitioner> createPractitioners() throws FHIRServerException {
        var practitionersToCreate = List.of(
                Practitioner.builder()
                        .meta(Meta.builder()
                                .profile(Canonical.of(PROFILE_BASE_URL + "vp-practitioner"))
                                .build())
                        .name(
                                HumanName.builder()
                                        .use(NameUse.OFFICIAL)
                                        .family("Müller")
                                        .given("Hubert", "Sebastian")
                                        .build(),
                                HumanName.builder()
                                        .use(NameUse.NICKNAME)
                                        .given("Hubsi")
                                        .build())
                        .build()
        );
        return Try.traverse(practitionersToCreate, practitionerPort::tryCreateAndRead).get().asJava();
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

    private List<Basic> createVaccinationSchemes() throws FHIRServerException {
        var vaccinationSchemesToCreate = List.of(
                Basic.builder()
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
                                                .build(),
                                        Extension.builder()
                                                .url("vaccine")
                                                .value(Reference.builder()
                                                        .reference("https://example.com/Medication/123")
                                                        .build())
                                                .build()
                                )
                                .build())
                        .build()
        );
        return Try.traverse(vaccinationSchemesToCreate, basicPort::tryCreateAndRead).get().asJava();
    }

    @Override
    public BootstrapResult bootstrap() throws FHIRServerException {
        var activeVaccinationSchemes = createActiveVaccinationSchemes();
        var immunizations = createImmunizations();
        var immunizationRecommendations = createImmunizationRecommendations();
        var medications = createMedications();
        var organizations = createOrganizations();
        var patients = createPatients();
        var populationRecommendations = createPopulationRecommendations();
        var practitioners = createPractitioners();
        var targetDiseases = createTargetDiseases();
        var vacationPlans = createVacationPlans();
        var vaccinationSchemes = createVaccinationSchemes();

        return BootstrapResult.builder()
                .activeVaccinationSchemes(activeVaccinationSchemes)
                .immunizations(immunizations)
                .immunizationRecommendations(immunizationRecommendations)
                .medications(medications)
                .organizations(organizations)
                .patients(patients)
                .populationRecommendations(populationRecommendations)
                .practitioners(practitioners)
                .targetDiseases(targetDiseases)
                .vacationPlans(vacationPlans)
                // TODO: Create vaccination doses
                .vaccinationDoses(List.of())
                .vaccinationSchemes(vaccinationSchemes)
                .build();
    }
}
