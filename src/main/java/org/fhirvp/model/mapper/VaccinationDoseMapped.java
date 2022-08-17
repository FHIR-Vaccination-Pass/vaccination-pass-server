package org.fhirvp.model.mapper;

import com.ibm.fhir.model.resource.Basic;
import com.ibm.fhir.model.type.Extension;
import com.ibm.fhir.path.FHIRPathNode;
import io.vavr.control.Try;
import org.fhirvp.Constants;

import java.util.Optional;

public class VaccinationDoseMapped extends FHIRResourceMapped<Basic> {

    private final Extension vaccinationDoseBaseExtension;

    private final Optional<VaccinationDoseSingleMapped> vaccinationDoseSingleExtension;
    private final Optional<VaccinationDoseRepeatingMapped> vaccinationDoseRepeatingMapped;

    public VaccinationDoseMapped(Basic basic) {
        super(basic);
        this.vaccinationDoseBaseExtension = createVaccinationDoseBaseExtension();
        this.vaccinationDoseSingleExtension = createVaccinationDoseSingleExtension();
        this.vaccinationDoseRepeatingMapped = createVaccinationDoseRepeatingExtension();
    }

    private Extension createVaccinationDoseBaseExtension() {
        String fhirPath = "extension.where(url = '" + Constants.PROFILE_BASE_URL + "vp-vaccination-dose-base-extension')";
        return super.createBaseExtension(fhirPath);
    }

    private Optional<VaccinationDoseSingleMapped> createVaccinationDoseSingleExtension() {
        String fhirPath = "extension.where(url = '" + Constants.PROFILE_BASE_URL + "vp-vaccination-dose-single-extension')";
        Extension extension = createNullableExtension(fhirPath);
        return (extension == null ? Optional.empty() : Optional.of(new VaccinationDoseSingleMapped(extension)));
    }

    private Optional<VaccinationDoseRepeatingMapped> createVaccinationDoseRepeatingExtension() {
        String fhirPath = "extension.where(url = '" + Constants.PROFILE_BASE_URL + "vp-vaccination-dose-repeating-extension')";
        Extension extension = createNullableExtension(fhirPath);
        return (extension == null ? Optional.empty() : Optional.of(new VaccinationDoseRepeatingMapped(extension)));
    }

    private Extension createNullableExtension(String fhirPath) {
        Optional<FHIRPathNode> optionalNode = Try.of(() -> fhirPathEvaluator.evaluate(_raw, fhirPath)).get()
                .stream().findFirst();
        if (optionalNode.isEmpty()) {
            return null;
        }
        return optionalNode.get().asElementNode().element().as(Extension.class);
    }

    public String getId() {
        return _raw.getId();
    }

    public String getVaccinationSchemeId() {
        String[] referenceParts = _raw.getSubject().getReference().getValue().split("/");
        return referenceParts[referenceParts.length - 1];
    }

    public boolean isProtected() {
        String fhirPath = "extension.where(url = 'isProtected').value";
        return super.createExactlyOne(vaccinationDoseBaseExtension, fhirPath)
                .as(com.ibm.fhir.model.type.Boolean.class).getValue();
    }

    public Optional<VaccinationDoseSingleMapped> getVaccinationDoseSingleExtension() {
        return vaccinationDoseSingleExtension;
    }

    public Optional<VaccinationDoseRepeatingMapped> getVaccinationDoseRepeatingMapped() {
        return vaccinationDoseRepeatingMapped;
    }

}
