package org.fhirvp.usecase;

import com.ibm.fhir.model.resource.Basic;
import com.ibm.fhir.model.resource.ImmunizationRecommendation;
import org.fhirvp.model.ForecastStatus;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

public interface FHIRResourceBuilderUseCase {

    Basic buildActiveVaccinationScheme(String patientId, String vaccinationSchemeId, String changeReason);

    ImmunizationRecommendation buildImmunizationRecommendation
            (
                    String patientId, String vaccineCode, String targetDiseaseCode, ForecastStatus forecastStatus,
                    Optional<Calendar> dateToGive, Optional<Calendar> earliestDateToGive, Optional<Calendar> latestDateToGive,
                    List<String> supportingImmunizationIds, boolean isDeactivated, Optional<String> fulfillingImmunizationId,
                    String supportingPopulationRecommendationId, String recommendedVaccinationDoseId
            );

}
