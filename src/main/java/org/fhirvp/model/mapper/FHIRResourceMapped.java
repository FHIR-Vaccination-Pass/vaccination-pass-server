package org.fhirvp.model.mapper;

import com.ibm.fhir.model.resource.Resource;
import com.ibm.fhir.model.type.Code;
import com.ibm.fhir.model.type.Element;
import com.ibm.fhir.model.type.Extension;
import com.ibm.fhir.model.type.Quantity;
import com.ibm.fhir.model.visitor.AbstractVisitable;
import com.ibm.fhir.path.FHIRPathNode;
import com.ibm.fhir.path.evaluator.FHIRPathEvaluator;
import io.vavr.control.Try;
import org.fhirvp.context.FHIRPathEvaluatorProducer;
import org.fhirvp.model.TimeUtils;

import java.util.Optional;

public abstract class FHIRResourceMapped<T extends AbstractVisitable> {

    protected final FHIRPathEvaluator fhirPathEvaluator;

    protected final T _raw;

    FHIRResourceMapped(T resource) {
        this.fhirPathEvaluator = FHIRPathEvaluatorProducer.getInstance();
        this._raw = resource;
    }

    protected Element createExactlyOne(Element entryPoint, String fhirPath) {
        return Try.of(() -> fhirPathEvaluator.evaluate(entryPoint, fhirPath)).get()
                .stream().findFirst().orElseThrow()
                .asElementNode().element();
    }

    protected Element createExactlyOne(Resource entryPoint, String fhirPath) {
        return Try.of(() -> fhirPathEvaluator.evaluate(entryPoint, fhirPath)).get()
                .stream().findFirst().orElseThrow()
                .asElementNode().element();
    }

    protected Optional<FHIRPathNode> createOptionalOne(Element entryPoint, String fhirPath) {
        return Try.of(() -> fhirPathEvaluator.evaluate(entryPoint, fhirPath)).get()
                .stream().findFirst();
    }

    protected Optional<FHIRPathNode> createOptionalOne(Resource entryPoint, String fhirPath) {
        return Try.of(() -> fhirPathEvaluator.evaluate(entryPoint, fhirPath)).get()
                .stream().findFirst();
    }

    protected Extension createBaseExtension(String fhirPath) {
        return Try.of(() -> fhirPathEvaluator.evaluate(_raw, fhirPath)).get()
                .stream().findFirst().orElseThrow()
                .asElementNode().element().as(Extension.class);
    }

    protected String createCodeValue(Element entryPoint, String fhirPath) {
        return createExactlyOne(entryPoint, fhirPath).as(Code.class).getValue();
    }

    protected String createCodeValue(Resource entryPoint, String fhirPath) {
        return createExactlyOne(entryPoint, fhirPath).as(Code.class).getValue();
    }

    protected Optional<Long> createAgeInDays(Element entryPoint, String fhirPath) {
        Optional<FHIRPathNode> optionalNode = createOptionalOne(entryPoint, fhirPath);
        if (optionalNode.isEmpty()) {
            return Optional.empty();
        }
        Quantity quantity = optionalNode.get().asElementNode().element().as(Quantity.class);
        return Optional.of(TimeUtils.convertQuantityAgeToDays(quantity));
    }

}
