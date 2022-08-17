package org.fhirvp.model.mapper;

import com.ibm.fhir.model.resource.Basic;
import com.ibm.fhir.model.type.Extension;
import org.fhirvp.Constants;
import org.fhirvp.model.VaccinationSchemeType;

import java.time.Duration;
import java.util.Optional;

public class VaccinationSchemeMapped extends FHIRResourceMapped<Basic> {

    private final Extension vaccinationSchemeExtension;

    public VaccinationSchemeMapped(Basic basic) {
        super(basic);
        this.vaccinationSchemeExtension = createVaccinationSchemeExtension();
    }

    private Extension createVaccinationSchemeExtension() {
        String fhirPath = "extension.where(url = '" + Constants.PROFILE_BASE_URL + "vp-vaccination-scheme-extension')";
        return super.createBaseExtension(fhirPath);
    }

    public String getId() {
        return _raw.getId();
    }

    public String getMedicationId() {
        String[] referenceParts = _raw.getSubject().getReference().getValue().split("/");
        return referenceParts[referenceParts.length - 1];
    }

    public String getName() {
        String fhirPath = "extension.where(url = 'name').value";
        return super.createExactlyOne(vaccinationSchemeExtension, fhirPath)
                .as(com.ibm.fhir.model.type.String.class).getValue();
    }

    public VaccinationSchemeType getType() {
        String fhirPath = "extension.where(url = 'type').value";
        String type = super.createExactlyOne(vaccinationSchemeExtension, fhirPath)
                .as(com.ibm.fhir.model.type.String.class).getValue();
        switch (type) {
            case "standard":
                return VaccinationSchemeType.STANDARD;
            case "fast":
                return VaccinationSchemeType.FAST;
            case "booster":
                return VaccinationSchemeType.BOOSTER;
            default:
                throw new IllegalArgumentException("Unsupported VaccinationSchemeType. Use codes from ValueSet 'https://fhir-vaccination-pass.github.io/fhir-implementation-guide/ValueSet/vp-vaccination-scheme-type-value-set'");
        }
    }

    public boolean isPreferred() {
        String fhirPath = "extension.where(url = 'isPreferred').value";
        return super.createExactlyOne(vaccinationSchemeExtension, fhirPath)
                .as(com.ibm.fhir.model.type.Boolean.class).getValue();
    }

    public Optional<Duration> getAgeStart() {
        String fhirPath = "extension.where(url = 'ageStart').value";
        return super.createAge(vaccinationSchemeExtension, fhirPath);
    }

    public Optional<Duration> getAgeEnd() {
        String fhirPath = "extension.where(url = 'ageEnd').value";
        return super.createAge(vaccinationSchemeExtension, fhirPath);
    }

}
