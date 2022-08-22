package org.fhirvp.model.mapper;

import com.ibm.fhir.model.resource.Basic;
import com.ibm.fhir.model.type.Extension;
import io.vavr.control.Try;
import org.fhirvp.Constants;
import org.fhirvp.model.AgeStartEnd;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PopulationRecommendationMapped extends FHIRResourceMapped<Basic> implements AgeStartEnd {

    private final Extension populationRecommendationExtension;

    private final List<LocationMapped> locationsMapped;

    public PopulationRecommendationMapped(Basic basic) {
        super(basic);
        this.populationRecommendationExtension = createPopulationRecommendationExtension();
        this.locationsMapped = createLocationMappers();
    }

    private Extension createPopulationRecommendationExtension() {
        String fhirPath = "extension.where(url = '" + Constants.PROFILE_BASE_URL + "vp-population-recommendation-extension')";
        return super.createBaseExtension(fhirPath);
    }

    private List<LocationMapped> createLocationMappers() {
        String fhirPath = "extension.where(url = '" + Constants.PROFILE_BASE_URL + "vp-location-extension')";
        return Try.of(() -> fhirPathEvaluator.evaluate(populationRecommendationExtension, fhirPath)).get()
                .stream().map(x -> new LocationMapped(x.asElementNode().element().as(Extension.class)))
                .collect(Collectors.toList());
    }

    public String getId() {
        return _raw.getId();
    }

    public String getTargetDiseaseCode() {
        String fhirPath = "extension.where(url = 'targetDisease').value.coding.where(system = 'http://hl7.org/fhir/sid/icd-10').code";
        return createCodeValue(populationRecommendationExtension, fhirPath);
    }

    @Override
    public Optional<Long> getAgeStartInDays() {
        String fhirPath = "extension.where(url = 'ageStart').value";
        return super.createAgeInDays(populationRecommendationExtension, fhirPath);
    }

    @Override
    public Optional<Long> getAgeEndInDays() {
        String fhirPath = "extension.where(url = 'ageEnd').value";
        return super.createAgeInDays(populationRecommendationExtension, fhirPath);
    }

    public List<LocationMapped> getLocationsMapped() {
        return locationsMapped;
    }

}
