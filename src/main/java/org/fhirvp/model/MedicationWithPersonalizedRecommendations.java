package org.fhirvp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.fhirvp.model.mapper.MedicationMapped;

import java.util.List;

@Getter
@AllArgsConstructor
public class MedicationWithPersonalizedRecommendations {

    MedicationMapped medication;
    List<PersonalizedRecommendation> personalizedRecommendations;

}
