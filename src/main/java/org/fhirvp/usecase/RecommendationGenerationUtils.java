package org.fhirvp.usecase;

import org.fhirvp.model.AgeStartEnd;
import org.fhirvp.model.mapper.PatientMapped;

public interface RecommendationGenerationUtils {

    boolean isAgeApplicable(PatientMapped patient, AgeStartEnd ageStartEnd);

}
