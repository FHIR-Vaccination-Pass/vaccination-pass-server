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

import java.time.Duration;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
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

    protected Optional<Duration> createAge(Element entryPoint, String fhirPath) {
        Optional<FHIRPathNode> optionalNode = createOptionalOne(entryPoint, fhirPath);
        if (optionalNode.isEmpty()) {
            return Optional.empty();
        }
        Quantity quantity = optionalNode.get().asElementNode().element().as(Quantity.class);
        return Optional.of(convertAgeToDuration(quantity));
    }

    protected static Calendar convertFullDateToCalendar(TemporalAccessor accessor) {
        int day = accessor.get(ChronoField.DAY_OF_MONTH);
        int month = accessor.get(ChronoField.MONTH_OF_YEAR);
        int year = accessor.get(ChronoField.YEAR);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month-1, day);
        return calendar;
    }

    protected static Duration convertAgeToDuration(Quantity quantity) {
        long factor;
        switch (quantity.getCode().getValue()) {
            case "min":
            case "h":
                throw new IllegalArgumentException("Codes 'min' and 'h' are unsupported for age. Use 'd', 'wk', 'mo' or 'a' instead.");
            case "d":
                factor = 1;
                break;
            case "wk":
                factor = 7;
                break;
            case "mo":
                factor = 30;
                break;
            case "a":
                factor = 365;
                break;
            default:
                throw new IllegalArgumentException("Unsupported code for age. Expected code from 'http://hl7.org/fhir/ValueSet/age-units' but got: " + quantity.getCode().getValue());
        }
        return Duration.ofDays(factor * quantity.getValue().getValue().longValue());
    }

}
