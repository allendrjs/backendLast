package org.rocs.osdrmsa.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Boundary conversion between JPA's java.util.Date columns and the
 * java.time.LocalDate used on DTOs facing the REST API. Entities keep
 * java.util.Date unchanged; only the controller/DTO layer sees LocalDate.
 */
public final class DateConversion {

    private DateConversion() {
    }

    public static LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static Date toDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
