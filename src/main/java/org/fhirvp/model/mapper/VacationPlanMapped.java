package org.fhirvp.model.mapper;

import com.ibm.fhir.model.resource.Basic;
import com.ibm.fhir.model.type.Extension;
import io.vavr.control.Try;
import org.fhirvp.Constants;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class VacationPlanMapped extends FHIRResourceMapped<Basic> {

    private final Extension vacationPlanExtension;

    private final List<LocationMapped> locationsMapped;

    public VacationPlanMapped(Basic basic) {
        super(basic);
        this.vacationPlanExtension = createVacationPlanExtension();
        this.locationsMapped = createLocationMappers();
    }

    private Extension createVacationPlanExtension() {
        String fhirPath = "extension.where(url = '" + Constants.PROFILE_BASE_URL + "vp-vacation-plan-extension')";
        return super.createBaseExtension(fhirPath);
    }

    private List<LocationMapped> createLocationMappers() {
        String fhirPath = "extension.where(url = '" + Constants.PROFILE_BASE_URL + "vp-location-extension')";
        return Try.of(() -> fhirPathEvaluator.evaluate(vacationPlanExtension, fhirPath)).get()
                .stream().map(x -> new LocationMapped(x.asElementNode().element().as(Extension.class)))
                .collect(Collectors.toList());
    }

    public String getId() {
        return _raw.getId();
    }

    public String getPatientId() {
        String[] referenceParts = _raw.getSubject().getReference().getValue().split("/");
        return referenceParts[referenceParts.length - 1];
    }

    public LocalDate getDepartureDate() {
        String fhirPath = "extension.where(url = 'departureDate').value";
        com.ibm.fhir.model.type.Date date = super.createExactlyOne(vacationPlanExtension, fhirPath)
                .as(com.ibm.fhir.model.type.Date.class);
        return LocalDate.from(date.getValue());
    }

    public List<LocationMapped> getLocationsMapped() {
        return locationsMapped;
    }

}
