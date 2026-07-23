package org.rocs.osdrmsa.controller.record.dto;

import org.rocs.osdrmsa.controller.common.dto.ActionSummary;
import org.rocs.osdrmsa.controller.common.dto.EmployeeSummary;
import org.rocs.osdrmsa.controller.common.dto.EnrollmentSummary;
import org.rocs.osdrmsa.controller.common.dto.OffenseSummary;
import org.rocs.osdrmsa.domain.record.RecordStatus;

import java.time.LocalDate;

public record RecordResponse(
        long recordId,
        EnrollmentSummary enrollment,
        EmployeeSummary employee,
        OffenseSummary offense,
        LocalDate dateOfViolation,
        ActionSummary action,
        LocalDate dateOfResolution,
        String remarks,
        RecordStatus status) {
}
