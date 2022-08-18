package org.fhirvp.usecase.impl.bootstrap;

import com.ibm.fhir.model.resource.*;
import com.ibm.fhir.model.type.*;
import com.ibm.fhir.model.type.code.AddressUse;
import com.ibm.fhir.model.type.code.AdministrativeGender;
import com.ibm.fhir.model.type.code.ImmunizationStatus;
import com.ibm.fhir.model.type.code.NameUse;
import com.ibm.fhir.path.evaluator.FHIRPathEvaluator;
import io.vavr.control.Try;
import org.fhirvp.ports.*;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;
import org.fhirvp.usecase.BootstrapUseCase;
import org.fhirvp.usecase.impl.bootstrap.medication.ComirnatyCreator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.lang.String;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.fhirvp.Constants.PROFILE_BASE_URL;

@ApplicationScoped
public class BootstrapUseCaseImpl implements BootstrapUseCase {
    @Inject
    ImmunizationPort immunizationPort;
    @Inject
    ImmunizationRecommendationPort immunizationRecommendationPort;
    @Inject
    OrganizationPort organizationPort;
    @Inject
    PatientPort patientPort;
    @Inject
    PractitionerPort practitionerPort;
    @Inject
    TargetDiseasePort targetDiseasePort;
    @Inject
    PopulationRecommendationPort populationRecommendationPort;
    @Inject
    VacationPlanPort vacationPlanPort;
    @Inject
    ActiveVaccinationSchemePort activeVaccinationSchemePort;

    @Inject
    ComirnatyCreator comirnatyCreator;

    @Inject
    FHIRPathEvaluator fhirPathEvaluator;

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
        return Try.traverse(targetDiseasesToCreate, targetDiseasePort::tryCreateAndRead).get().asJava();
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
                                                                        .value("U07.1")
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
                                                .build()
                                )
                                .build())
                        .build()
        );
        return Try.traverse(populationRecommendationsToCreate, populationRecommendationPort::tryCreateAndRead).get().asJava();
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

    private Basic createVacationPlan(
            Patient patient,
            Code countryCode,
            Code stateCode,
            Date departureDate
    ) throws FHIRServerException {
        return vacationPlanPort.createAndRead(Basic.builder()
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
                .subject(Reference.builder()
                        .reference("Patient/" + patient.getId())
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
                                                        .code(countryCode)
                                                        .build())
                                                .build())
                                        .build())
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
                        .extension(Extension.builder()
                                .url("departureDate")
                                .value(departureDate)
                                .build())
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
            Basic populationRecommendation,
            CodeableConcept vaccineCode,
            Code forecastStatus,
            DateTime date,
            DateTime earliestDateToGive,
            DateTime latestDateToGive,
            List<Immunization> supportingImmunizations,
            Basic recommendedVaccinationDose

    ) throws FHIRServerException {
        var targetDisease = Try.of(() -> fhirPathEvaluator.evaluate(
                populationRecommendation,
                "extension('" + PROFILE_BASE_URL + "vp-population-recommendation-extension" + "')" +
                        ".extension('" + "targetDisease" + "').value"
        )).get().stream().findFirst().get().asElementNode().element().as(CodeableConcept.class);

        return immunizationRecommendationPort.createAndRead(ImmunizationRecommendation.builder()
                .meta(Meta.builder()
                        .profile(Canonical.of(PROFILE_BASE_URL + "vp-immunization-recommendation"))
                        .build())
                .patient(Reference.builder()
                        .reference("Patient/" + patient.getId())
                        .build())
                .date(date)
                .recommendation(ImmunizationRecommendation.Recommendation.builder()
                        .vaccineCode(vaccineCode)
                        .targetDisease(targetDisease)
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

    private Basic createActiveVaccinationScheme(Patient patient, Basic vaccinationScheme) throws FHIRServerException {
        return activeVaccinationSchemePort.createAndRead(Basic.builder()
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
                .subject(Reference.builder()
                        .reference("Patient/" + patient.getId())
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
                                        .url("changeReason")
                                        .value(Markdown.of("This is a reason."))
                                        .build()
                        )
                        .build())
                .build());
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

        var targetDiseases = createTargetDiseases();
        result.getTargetDiseases().addAll(targetDiseases);

        var populationRecommendations = createPopulationRecommendations();
        result.getPopulationRecommendations().addAll(populationRecommendations);

        var organizationBioNTech = createOrganization("BioNTech Manufacturing GmbH");
        result.getOrganizations().add(organizationBioNTech);

        var comirnatyResult = comirnatyCreator.create(organizationBioNTech);
        var comirnatyMedication = comirnatyResult.getMedication();
        var comirnatyVaccinationDoses = comirnatyResult.getVaccinationDoses();
        var comirnatyVaccinationSchemes = comirnatyResult.getVaccinationSchemes();
        result.getMedications().add(comirnatyMedication);
        result.getVaccinationDoses().addAll(comirnatyVaccinationDoses);
        result.getVaccinationSchemes().addAll(comirnatyVaccinationSchemes);

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

            var vacationPlans = List.of(
                    createVacationPlan(
                            patient,
                            Code.of("BR"),
                            Code.of("BR-RJ"),
                            Date.of(LocalDate.of(2022, 11, 1))
                    )
            );
            result.getVacationPlans().addAll(vacationPlans);

            var immunizations = List.of(
                    createImmunization(
                            patient,
                            practitionerDrHouse,
                            comirnatyMedication,
                            comirnatyVaccinationDoses.get(0),
                            "COMIRNATY-001",
                            DateTime.of(LocalDate.of(2020, 1, 1))
                    ),
                    createImmunization(
                            patient,
                            practitionerDrHouse,
                            comirnatyMedication,
                            comirnatyVaccinationDoses.get(1),
                            "COMIRNATY-002",
                            DateTime.of(LocalDate.of(2020, 2, 1))
                    ),
                    createImmunization(
                            patient,
                            practitionerDrHouse,
                            comirnatyMedication,
                            comirnatyVaccinationDoses.get(2),
                            "COMIRNATY-003",
                            DateTime.of(LocalDate.of(2020, 8, 1))
                    )
            );
            result.getImmunizations().addAll(immunizations);

            var immunizationRecommendations = List.of(
                    createImmunizationRecommendation(
                            patient,
                            populationRecommendations.get(0),
                            comirnatyMedication.getCode(),
                            Code.of("complete"),
                            DateTime.of(LocalDate.of(2020, 8, 1)),
                            DateTime.of(LocalDate.of(2021, 2, 1)),
                            DateTime.of(LocalDate.of(2021, 4, 1)),
                            immunizations,
                            comirnatyVaccinationDoses.get(2)
                    )
            );
            result.getImmunizationRecommendations().addAll(immunizationRecommendations);

            var activeVaccinationSchemes = List.of(
                    createActiveVaccinationScheme(
                            patient,
                            comirnatyResult.getVaccinationSchemes().get(0)
                    )
            );
            result.getActiveVaccinationSchemes().addAll(activeVaccinationSchemes);
        }

        return result;
    }
}
