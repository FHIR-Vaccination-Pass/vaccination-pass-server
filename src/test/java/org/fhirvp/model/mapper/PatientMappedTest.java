package org.fhirvp.model.mapper;

import com.ibm.fhir.model.resource.Patient;
import com.ibm.fhir.model.type.*;
import com.ibm.fhir.model.type.code.AddressUse;
import com.ibm.fhir.model.type.code.AdministrativeGender;
import com.ibm.fhir.model.type.code.NameUse;
import org.junit.jupiter.api.Test;

import java.lang.String;
import java.time.format.DateTimeFormatter;


class PatientMappedTest {

    static final String BASE_URL = "https://fhir-vaccination-pass.github.io/fhir-implementation-guide/StructureDefinition/";
    static final String PROFILE_URL = BASE_URL + "vp-patient";

    @Test
    void test() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        PatientMapped patientMapped = new PatientMapped(generatePatient());

        assert patientMapped.isActive();
        assert patientMapped.getBirthDate().format(dateTimeFormatter).equals("01.01.2000");
        assert !patientMapped.isDeceased();
        assert !patientMapped.isPregnant();
        assert patientMapped.getKeycloakUsername().equals("müller");
        assert patientMapped.getAddressMapped().get(0).getCountryCode().equals("DE");
        assert patientMapped.getAddressMapped().get(0).getStateCode().equals("DE-BY");
    }

    private Patient generatePatient() {
        return Patient.builder()
                .meta(Meta.builder()
                        .profile(Canonical.of(PROFILE_URL))
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
                                        .url(BASE_URL + "vp-state-code-extension")
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
                                        .url(BASE_URL + "vp-country-code-extension")
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
                        .url(BASE_URL + "vp-patient-keycloak-username-extension")
                        .value("müller")
                        .build())
                .extension(Extension.builder()
                        .url(BASE_URL + "vp-patient-is-pregnant-extension")
                        .value(false)
                        .build())
                .build();
    }

}
