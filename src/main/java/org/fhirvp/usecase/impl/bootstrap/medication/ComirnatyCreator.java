package org.fhirvp.usecase.impl.bootstrap.medication;

import com.ibm.fhir.model.resource.Basic;
import com.ibm.fhir.model.resource.Medication;
import com.ibm.fhir.model.resource.Organization;
import com.ibm.fhir.model.type.*;
import io.vavr.control.Try;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.fhirvp.Constants.PROFILE_BASE_URL;

@ApplicationScoped
public class ComirnatyCreator extends MedicationCreator {
    @Override
    public MedicationCreateResult create(Organization organization) throws FHIRServerException {
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
                        .value("ComirnatyCreator")
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

        return MedicationCreateResult.builder()
                .medication(medication)
                .vaccinationDoses(vaccinationSchemeStandardDoses)
                .vaccinationSchemes(Stream.of(
                        vaccinationSchemeStandardDoses.stream()
                ).flatMap(Function.identity()).collect(Collectors.toList()))
                .build();
    }
}
