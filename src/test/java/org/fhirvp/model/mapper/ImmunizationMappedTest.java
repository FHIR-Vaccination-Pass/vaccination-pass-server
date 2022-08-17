package org.fhirvp.model.mapper;

import com.ibm.fhir.model.resource.Immunization;
import com.ibm.fhir.model.type.*;
import com.ibm.fhir.model.type.code.ImmunizationStatus;
import org.junit.jupiter.api.Test;

import java.lang.String;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

class ImmunizationMappedTest {

    static final String BASE_URL = "https://fhir-vaccination-pass.github.io/fhir-implementation-guide/StructureDefinition/";
    static final String PROFILE_URL = BASE_URL + "vp-immunization";

    @Test
    void test() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        ImmunizationMapped immunizationMapped = new ImmunizationMapped(generateImmunization());

        assert immunizationMapped.getStatus().equals(ImmunizationStatus.COMPLETED);
        assert immunizationMapped.getVaccineCode().equals("vaccine-abc");
        assert immunizationMapped.getPatientId().equals("123");
        assert dateFormat.format(immunizationMapped.getOccurrence().getTime()).equals("10.07.2022");
        assert immunizationMapped.getAdministeredVaccinationDoseId().equals("789");
    }

    private Immunization generateImmunization() {
        return Immunization.builder()
                .meta(Meta.builder()
                        .profile(Canonical.of(PROFILE_URL))
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
                        .url(BASE_URL + "vp-administered-vaccination-dose")
                        .value(Reference.builder()
                                .reference("https://example.com/Basic/789")
                                .build())
                        .build())
                .build();
    }

}
