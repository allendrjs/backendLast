package org.rocs.osdrmsa.service.audit;

import org.rocs.osdrmsa.domain.audit.AuditLog;

import java.util.List;

public interface AuditLogService {

    /**
     * Records an action against an entity. Actor is resolved from the
     * current SecurityContext, not passed in, so callers can't misattribute
     * an entry to the wrong user.
     */
    void log(String action, String entityType, String entityId, String details);

    List<AuditLog> getHistory(String entityType, String entityId);
}
