package org.rocs.osdrmsa.dto.request;

import java.time.LocalDate;

public record AcademicPeriodRequest(
        String label,
        LocalDate startDate,
        LocalDate endDate,
        LocalDate appealDeadline
) {
}
