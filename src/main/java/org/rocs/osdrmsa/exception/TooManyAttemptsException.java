package org.rocs.osdrmsa.exception;

/**
 * Thrown when a username has exceeded the allowed number of failed login
 * attempts within the lockout window. Distinct from AccountLockedException,
 * which represents a persistent, OSD-staff-managed lock - this one clears
 * itself once the LoginAttemptService cache window expires.
 */
public class TooManyAttemptsException extends RuntimeException {
    public TooManyAttemptsException(String message) {
        super(message);
    }
}
