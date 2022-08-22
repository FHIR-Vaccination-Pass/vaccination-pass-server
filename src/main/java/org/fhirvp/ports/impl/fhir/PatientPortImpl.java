package org.fhirvp.ports.impl.fhir;

import com.ibm.fhir.client.FHIRParameters;
import com.ibm.fhir.model.resource.Basic;
import com.ibm.fhir.model.resource.Patient;
import org.fhirvp.model.mapper.PatientMapped;
import org.fhirvp.model.mapper.VacationPlanMapped;
import org.fhirvp.ports.PatientPort;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class PatientPortImpl extends FHIRResourcePortImpl<Patient> implements PatientPort {
    PatientPortImpl() {
        resourceClass = Patient.class;
        resourceName = "Patient";
        resourcePath = "/patient";
    }

    public List<PatientMapped> searchReturnList(FHIRParameters searchParameters) throws FHIRServerException {
        return this.search(searchParameters).getEntry()
                .stream().map(entry -> new PatientMapped(entry.getResource().as(Patient.class)))
                .collect(Collectors.toList());
    }

}
