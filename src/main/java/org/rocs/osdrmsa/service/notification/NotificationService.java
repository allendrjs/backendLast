package org.rocs.osdrmsa.service.notification;

/**
 * Sends email notifications to employees about decisions made on their
 * submitted requests/appeals. Implementations must never throw — a
 * notification failure must not block or roll back the underlying
 * decision workflow.
 */
public interface NotificationService {

    void notifyRequestDecision(
            String recipientEmail,
            String recipientName,
            long requestId,
            String decision,
            String remarks
    );
}
