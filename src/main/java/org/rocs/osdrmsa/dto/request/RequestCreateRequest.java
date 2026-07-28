package org.rocs.osdrmsa.dto.request;

/**
 * Payload for a Department Head filing a new student record request.
 * employeeId is not accepted here - it's resolved server-side from the
 * caller's own JWT identity so a Dept Head can't file a request under
 * someone else's name.
 */
public record RequestCreateRequest(
        String details,
        String message,
        String type
) {
}
