package org.fhirvp.ports.impl.fhir;

import com.ibm.fhir.client.FHIRParameters;
import com.ibm.fhir.model.resource.Basic;
import com.ibm.fhir.model.resource.Immunization;
import org.fhirvp.model.mapper.ImmunizationMapped;
import org.fhirvp.model.mapper.VacationPlanMapped;
import org.fhirvp.ports.ImmunizationPort;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ImmunizationPortImpl extends FHIRResourcePortImpl<Immunization> implements ImmunizationPort {
    ImmunizationPortImpl() {
        resourceClass = Immunization.class;
        resourceName = "Immunization";
        resourcePath = "/immunization";
    }

    public List<ImmunizationMapped> searchReturnList(FHIRParameters searchParameters) throws FHIRServerException {
        return this.search(searchParameters).getEntry()
                .stream().map(entry -> new ImmunizationMapped(entry.getResource().as(Immunization.class)))
                .collect(Collectors.toList());
    }

}
