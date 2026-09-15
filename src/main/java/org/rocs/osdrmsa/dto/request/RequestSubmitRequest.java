package org.rocs.osdrmsa.dto.request;

public record RequestSubmitRequest(
        String details,
        String message,
        String type
) {
}