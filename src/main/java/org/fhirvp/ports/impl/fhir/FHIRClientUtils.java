package org.fhirvp.ports.impl.fhir;

import com.ibm.fhir.client.FHIRResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.fhirvp.ports.impl.fhir.exception.FHIRServerException;

import java.util.Set;
import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FHIRClientUtils {


    @FunctionalInterface
    interface  ThrowingSupplier<T> {
        T get() throws Exception;
    }

    static Set<Integer> createAllowedStatus = Set.of(201);
    static Set<Integer> readAllowedStatus = Set.of(200);
    static Set<Integer> updateAllowedStatus = Set.of(200, 201);
    static Set<Integer> deleteAllowedStatus = Set.of(200, 202, 204);

    public static FHIRResponse wrapRequest(
            ThrowingSupplier<FHIRResponse> requestFunc,
            Set<Integer> allowedStatus,
            String errorMessage
    ) throws FHIRServerException {
        try {
            var response = requestFunc.get();

            if (!allowedStatus.contains(response.getStatus())) {
                throw new FHIRServerException(errorMessage + " with status " + response.getStatus(), response);
            }

            return response;
        } catch (Exception err) {
            throw new FHIRServerException(errorMessage, err);
        }
    }

    public static FHIRResponse wrapCreate(ThrowingSupplier<FHIRResponse> requestFunc, String errorMessage) throws FHIRServerException {
        return wrapRequest(requestFunc, createAllowedStatus, errorMessage);
    }

    public static FHIRResponse wrapRead(ThrowingSupplier<FHIRResponse> requestFunc, String errorMessage) throws FHIRServerException {
        return wrapRequest(requestFunc, readAllowedStatus, errorMessage);
    }

    public static FHIRResponse wrapUpdate(ThrowingSupplier<FHIRResponse> requestFunc, String errorMessage) throws FHIRServerException {
        return wrapRequest(requestFunc, updateAllowedStatus, errorMessage);
    }

    public static FHIRResponse wrapDelete(ThrowingSupplier<FHIRResponse> requestFunc, String errorMessage) throws FHIRServerException {
        return wrapRequest(requestFunc, deleteAllowedStatus, errorMessage);
    }

    public static <T> T rethrow(ThrowingSupplier<T> supplier, String errorMessage) throws FHIRServerException {
        try {
            return supplier.get();
        } catch (Exception err) {
            throw new FHIRServerException(errorMessage, err);
        }
    }
}
