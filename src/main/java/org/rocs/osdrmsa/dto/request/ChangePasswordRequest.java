package org.rocs.osdrmsa.dto.request;

public record ChangePasswordRequest(
        String currentPassword,
        String newPassword
) {
}
