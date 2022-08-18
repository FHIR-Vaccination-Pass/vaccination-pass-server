package org.fhirvp;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

    public static final String PROFILE_BASE_URL = "https://fhir-vaccination-pass.github.io/fhir-implementation-guide/StructureDefinition/";

    public static final String DATE_CRITERION_DATE = "30980-7";
    public static final String DATE_CRITERION_EARLIEST = "30981-5";
    public static final String DATE_CRITERION_LATEST = "59777-3";

}
