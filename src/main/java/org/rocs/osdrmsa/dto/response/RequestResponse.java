package org.rocs.osdrmsa.dto.response;

import org.rocs.osdrmsa.domain.request.RequestStatus;

import java.time.LocalDate;

public record RequestResponse(
        Long requestId,
        String employeeId,
        String employeeName,
        String details,
        String message,
        String type,
        RequestStatus status,
        LocalDate dateProcessed,
        String remarks
) {
}
