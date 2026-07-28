package org.rocs.osdrmsa.service.record;

/**
 * Simple carrier for a "how many times has this offense occurred" row.
 * Kept separate from the JPA projection (OffenseFrequencyProjection) so the
 * service layer doesn't leak a repository-layer interface to its callers.
 */
public record OffenseFrequency(String offense, long total) {
}
