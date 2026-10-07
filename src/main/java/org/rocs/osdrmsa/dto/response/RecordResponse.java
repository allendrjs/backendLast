package org.rocs.osdrmsa.dto.response;

import org.rocs.osdrmsa.domain.record.RecordStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record RecordResponse(
        Long recordId,
        Long enrollmentId,
        String studentId,
        String studentName,
        String schoolYear,
        String employeeId,
        String employeeName,
        Long offenseId,
        String offenseName,
        LocalDate dateOfViolation,
        Long actionId,
        String actionName,
        LocalDateTime dateOfResolution,
        String remarks,
        RecordStatus status
) {
}
