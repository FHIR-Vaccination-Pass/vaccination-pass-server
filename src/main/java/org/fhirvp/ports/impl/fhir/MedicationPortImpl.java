package org.fhirvp.ports.impl.fhir;

import com.ibm.fhir.client.FHIRParameters;
import com.ibm.fhir.model.resource.Basic;
import com.ibm.fhir.model.resource.Medication;
import org.fhirvp.model.mapper.MedicationMapped;
import org.fhirvp.model.mapper.VacationPlanMapped;
import org.fhirvp.ports.MedicationPort;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class MedicationPortImpl extends FHIRResourcePortImpl<Medication> implements MedicationPort {
    MedicationPortImpl() {
        resourceClass = Medication.class;
        resourceName = "Medication";
        resourcePath = "/medication";
    }

    public List<MedicationMapped> searchReturnList(FHIRParameters searchParameters) throws FHIRServerException {
        return this.search(searchParameters).getEntry()
                .stream().map(entry -> new MedicationMapped(entry.getResource().as(Medication.class)))
                .collect(Collectors.toList());
    }

}
