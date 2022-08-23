package org.fhirvp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.fhirvp.model.mapper.ImmunizationMapped;
import org.fhirvp.model.mapper.MedicationMapped;

import java.util.List;

@Getter
@AllArgsConstructor
public class AdministeredMedication {

    MedicationMapped medication;
    List<ImmunizationMapped> immunizations;
    List<PersonalizedRecommendation> personalizedRecommendations;

}
