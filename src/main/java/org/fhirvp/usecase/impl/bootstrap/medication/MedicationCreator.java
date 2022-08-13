package org.fhirvp.usecase.impl.bootstrap.medication;

import com.ibm.fhir.model.resource.Organization;
import org.fhirvp.ports.BasicPort;
import org.fhirvp.ports.MedicationPort;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;

import javax.inject.Inject;

public abstract class MedicationCreator {
    @Inject
    BasicPort basicPort;

    @Inject
    MedicationPort medicationPort;

    public abstract MedicationCreateResult create(Organization organization) throws FHIRServerException;
}
