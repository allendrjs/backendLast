package org.rocs.osdrmsa.domain.audit;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

/**
 * Immutable trail of who did what to which disciplinary record, appeal, or
 * request. Written by AuditLogServiceImpl only - there is deliberately no
 * update/delete path exposed anywhere in the API.
 */
@Entity
@Table(name = "AUDIT_LOG")
@Data
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AUDIT_LOG_ID", nullable = false, updatable = false)
    private long auditLogId;

    @Column(name = "ACTOR_USERNAME", nullable = false, updatable = false)
    private String actorUsername;

    @Column(name = "ACTION", nullable = false, updatable = false)
    private String action;

    @Column(name = "ENTITY_TYPE", nullable = false, updatable = false)
    private String entityType;

    @Column(name = "ENTITY_ID", nullable = false, updatable = false)
    private String entityId;

    @Column(name = "DETAILS", updatable = false)
    private String details;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "OCCURRED_AT", nullable = false, updatable = false)
    private Date occurredAt;
}
