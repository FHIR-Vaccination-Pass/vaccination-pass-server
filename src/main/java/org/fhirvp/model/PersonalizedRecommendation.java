package org.fhirvp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.fhirvp.model.mapper.PopulationRecommendationMapped;
import org.fhirvp.model.mapper.TargetDiseaseMapped;
import org.fhirvp.model.mapper.VacationPlanMapped;

import java.util.List;

@Getter
@AllArgsConstructor
public class PersonalizedRecommendation {

    PopulationRecommendationMapped populationRecommendation;
    TargetDiseaseMapped targetDisease;
    List<VacationPlanMapped> vacationPlans;

}
