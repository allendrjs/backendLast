package org.rocs.osdrmsa.controller.audit;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.audit.AuditLog;
import org.rocs.osdrmsa.service.audit.AuditLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit-log")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRATOR')")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<List<AuditLog>> getHistory(
            @RequestParam String entityType, @RequestParam String entityId) {
        return ResponseEntity.ok(auditLogService.getHistory(entityType, entityId));
    }
}
