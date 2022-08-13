package org.fhirvp.usecase.impl.bootstrap.medication;

import com.ibm.fhir.model.resource.Basic;
import com.ibm.fhir.model.resource.Medication;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Builder
@Value
public class MedicationCreateResult {
    Medication medication;
    List<Basic> vaccinationDoses;
    List<Basic> vaccinationSchemes;
}
