package org.rocs.osdrmsa.service.audit.impl;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.audit.AuditLog;
import org.rocs.osdrmsa.repository.audit.AuditLogRepository;
import org.rocs.osdrmsa.service.audit.AuditLogService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private static final String SYSTEM_ACTOR = "system";

    private final AuditLogRepository auditLogRepository;

    @Override
    public void log(String action, String entityType, String entityId, String details) {

        AuditLog entry = new AuditLog();
        entry.setActorUsername(currentUsername());
        entry.setAction(action);
        entry.setEntityType(entityType);
        entry.setEntityId(entityId);
        entry.setDetails(details);
        entry.setOccurredAt(new Date());

        auditLogRepository.save(entry);
    }

    @Override
    public List<AuditLog> getHistory(String entityType, String entityId) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByOccurredAtDesc(
                entityType, entityId);
    }

    private String currentUsername() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null && authentication.getName() != null)
                ? authentication.getName()
                : SYSTEM_ACTOR;
    }
}
