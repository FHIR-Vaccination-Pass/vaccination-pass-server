package org.fhirvp.model;

import com.ibm.fhir.model.type.Quantity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Period;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TimeUtils {

    public static final int DAYS_OF_WEEK = 7;
    public static final int DAYS_OF_MONTH = 30;
    public static final int DAYS_OF_YEAR = 365;

    public static long convertQuantityAgeToDays(Quantity quantity) {
        long factor;
        switch (quantity.getCode().getValue()) {
            case "min":
            case "h":
                throw new IllegalArgumentException("Codes 'min' and 'h' are unsupported for age. Use 'd', 'wk', 'mo' or 'a' instead.");
            case "d":
                factor = 1;
                break;
            case "wk":
                factor = DAYS_OF_WEEK;
                break;
            case "mo":
                factor = DAYS_OF_MONTH;
                break;
            case "a":
                factor = DAYS_OF_YEAR;
                break;
            default:
                throw new IllegalArgumentException("Unsupported code for age. Expected code from 'http://hl7.org/fhir/ValueSet/age-units' but got: " + quantity.getCode().getValue());
        }
        return factor * quantity.getValue().getValue().longValue();
    }

    public static long convertPeriodToDays(Period period) {
        return period.getDays()
                + ((long) period.getMonths()) * DAYS_OF_MONTH
                + ((long) period.getYears()) * DAYS_OF_YEAR;
    }

}
