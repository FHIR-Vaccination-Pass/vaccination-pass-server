package org.fhirvp.usecase;

import org.fhirvp.model.PersonalizedRecommendation;
import org.fhirvp.model.mapper.MedicationMapped;
import org.fhirvp.model.mapper.PatientMapped;

import java.util.List;

public interface RecommendationGeneratorUnvaccinatedUseCase {

    void generate(PatientMapped patient, List<MedicationMapped> medicationsSorted, List<PersonalizedRecommendation> unvaccinatedPersonalizedRecommendations);

}
