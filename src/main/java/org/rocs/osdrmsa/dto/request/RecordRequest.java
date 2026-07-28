package org.rocs.osdrmsa.dto.request;

import java.time.LocalDate;

/** Payload for filing a new violation record. Status is always set to PENDING server-side. */
public record RecordRequest(
        Long enrollmentId,
        String employeeId,
        Long offenseId,
        LocalDate dateOfViolation
) {
}
