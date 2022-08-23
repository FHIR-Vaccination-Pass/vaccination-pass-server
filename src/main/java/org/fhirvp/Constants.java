package org.fhirvp;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

    public static final String PROFILE_BASE_URL = "https://fhir-vaccination-pass.github.io/fhir-implementation-guide/StructureDefinition/";

    public static final String DATE_CRITERION_DATE = "30980-7";
    public static final String DATE_CRITERION_EARLIEST = "30981-5";
    public static final String DATE_CRITERION_LATEST = "59777-3";

    public static final int SEARCH_BUNDLE_COUNT = 1000;

    public static final int DAYS_VACATION_IMMUNIZATION_DONE_BEFORE_DEPARTURE = 3;

}
