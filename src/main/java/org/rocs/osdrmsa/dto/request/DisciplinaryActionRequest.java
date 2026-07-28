package org.rocs.osdrmsa.dto.request;

/**
 * actionId is required on create - ACTIONID has no @GeneratedValue in the
 * DB script, it's a manually assigned key (see DisciplinaryActionServiceImpl).
 * Ignored on update, since the path variable owns it there.
 */
public record DisciplinaryActionRequest(Long actionId, String action, String description) {
}
