package org.fhirvp.model.builder;

import com.ibm.fhir.model.resource.Basic;
import com.ibm.fhir.model.resource.ImmunizationRecommendation;
import org.fhirvp.model.ForecastStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FHIRResourceBuilder {

    Basic buildActiveVaccinationScheme(String patientId, String vaccinationSchemeId, String changeReason);

    ImmunizationRecommendation buildImmunizationRecommendation
            (
                    String patientId,
                    String vaccineCode,
                    String targetDiseaseCode,
                    ForecastStatus forecastStatus,
                    Optional<LocalDate> dateToGive,
                    Optional<LocalDate> earliestDateToGive,
                    Optional<LocalDate> latestDateToGive,
                    List<String> supportingImmunizationIds,
                    boolean isDeactivated, Optional<String> fulfillingImmunizationId,
                    String supportingPopulationRecommendationId,
                    String recommendedVaccinationDoseId
            );

}
