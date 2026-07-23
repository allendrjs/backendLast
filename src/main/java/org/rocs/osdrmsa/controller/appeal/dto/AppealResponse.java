package org.rocs.osdrmsa.controller.appeal.dto;

import org.rocs.osdrmsa.controller.common.dto.EnrollmentSummary;
import org.rocs.osdrmsa.controller.common.dto.RecordSummary;
import org.rocs.osdrmsa.domain.appeal.AppealStatus;

import java.time.LocalDate;

public record AppealResponse(
        long appealId,
        RecordSummary record,
        EnrollmentSummary enrollment,
        String message,
        LocalDate dateFiled,
        AppealStatus status,
        LocalDate dateProcessed,
        String remarks) {
}
