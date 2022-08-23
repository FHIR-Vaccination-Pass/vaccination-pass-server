package org.fhirvp.usecase.impl.recommendation;

import org.fhirvp.model.AgeStartEnd;
import org.fhirvp.model.mapper.PatientMapped;
import org.fhirvp.usecase.RecommendationGenerationUtils;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RecommendationGenerationUtilsImpl implements RecommendationGenerationUtils {

    public boolean isAgeApplicable(PatientMapped patient, AgeStartEnd ageStartEnd) {
        return (ageStartEnd.getAgeStartInDays().isEmpty() || ageStartEnd.getAgeStartInDays().get() <= patient.getCurrentAgeInDays())
                && (ageStartEnd.getAgeEndInDays().isEmpty() || ageStartEnd.getAgeEndInDays().get() >= patient.getCurrentAgeInDays());
    }

}
