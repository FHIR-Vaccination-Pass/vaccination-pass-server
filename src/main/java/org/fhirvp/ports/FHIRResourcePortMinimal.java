package org.fhirvp.ports;

import com.ibm.fhir.model.resource.Resource;
import io.vavr.control.Try;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;

public interface FHIRResourcePortMinimal<T extends Resource> {

    T read(String id) throws FHIRServerException;
    Try<T> tryRead(String id);

}
