package org.rocs.osdrmsa.controller.request.dto;

import org.rocs.osdrmsa.domain.request.RequestStatus;

import java.time.LocalDate;

public record RequestResponse(
        long requestId,
        String employeeId,
        String details,
        String message,
        String type,
        RequestStatus status,
        LocalDate dateProcessed,
        String remarks) {
}
