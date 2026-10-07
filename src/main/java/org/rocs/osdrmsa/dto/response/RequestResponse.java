package org.rocs.osdrmsa.dto.response;

import org.rocs.osdrmsa.domain.request.RequestStatus;

import java.time.LocalDateTime;

public record RequestResponse(
        long requestId,
        String employeeId,
        String details,
        String message,
        String type,
        RequestStatus status,
        LocalDateTime dateFiled,
        LocalDateTime dateProcessed,
        String remarks,
        String graduationEligibility,
        String deliveryMethod) {
}