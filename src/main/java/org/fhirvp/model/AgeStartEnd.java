package org.fhirvp.model;

import java.util.Optional;

public interface AgeStartEnd {

    Optional<Long> getAgeStartInDays();

    Optional<Long> getAgeEndInDays();

}
