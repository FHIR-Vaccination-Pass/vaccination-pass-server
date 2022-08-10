package org.fhirvp.usecase.impl;

import com.ibm.fhir.model.resource.Patient;
import com.ibm.fhir.model.type.*;
import com.ibm.fhir.model.type.code.AddressUse;
import com.ibm.fhir.model.type.code.AdministrativeGender;
import com.ibm.fhir.model.type.code.NameUse;
import org.fhirvp.ports.FHIRResourcePort;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;
import org.fhirvp.usecase.BootstrapUseCase;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.lang.String;
import java.util.List;

@ApplicationScoped
public class BootstrapUseCaseImpl implements BootstrapUseCase {
    static final String PROFILE_BASE_URL = "https://fhir-vaccination-pass.github.io/fhir-implementation-guide/StructureDefinition/";

    @Inject
    FHIRResourcePort<Patient> patientPort;

    @Override
    public BootstrapResult bootstrap() throws FHIRServerException {
        var patient = Patient.builder()
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
                .build();

        var patientId = patientPort.create(patient);
        patient = patientPort.read(patientId);

        return new BootstrapResult(List.of(patient));
    }
}
