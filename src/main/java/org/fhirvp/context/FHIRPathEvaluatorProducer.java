package org.fhirvp.context;

import com.ibm.fhir.path.evaluator.FHIRPathEvaluator;

import javax.enterprise.inject.Produces;

public class FHIRPathEvaluatorProducer {
    @Produces
    public static FHIRPathEvaluator getInstance() {
        return FHIRPathEvaluator.evaluator();
    }
}
