package org.rocs.osdrmsa.dto.response;

public record LockedAccountResponse(
        String username,
        String role,
        int failedLoginAttempts,
        boolean locked,
        boolean active
) {
}
