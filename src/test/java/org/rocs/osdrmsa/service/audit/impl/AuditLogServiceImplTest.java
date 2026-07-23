package org.rocs.osdrmsa.service.audit.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rocs.osdrmsa.domain.audit.AuditLog;
import org.rocs.osdrmsa.repository.audit.AuditLogRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceImplTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogServiceImpl auditLogService;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void log_capturesAuthenticatedActor() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("prefect1", null, List.of()));
        when(auditLogRepository.save(any(AuditLog.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        auditLogService.log("RECORD_CREATED", "Record", "123", "note");

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());

        AuditLog saved = captor.getValue();
        assertThat(saved.getActorUsername()).isEqualTo("prefect1");
        assertThat(saved.getAction()).isEqualTo("RECORD_CREATED");
        assertThat(saved.getEntityType()).isEqualTo("Record");
        assertThat(saved.getEntityId()).isEqualTo("123");
        assertThat(saved.getOccurredAt()).isNotNull();
    }

    @Test
    void log_fallsBackToSystemActorWhenUnauthenticated() {
        when(auditLogRepository.save(any(AuditLog.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        auditLogService.log("RECORD_CREATED", "Record", "123", null);

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());

        assertThat(captor.getValue().getActorUsername()).isEqualTo("system");
    }

    @Test
    void getHistory_delegatesToRepository() {
        AuditLog entry = new AuditLog();
        when(auditLogRepository.findByEntityTypeAndEntityIdOrderByOccurredAtDesc("Record", "123"))
                .thenReturn(List.of(entry));

        List<AuditLog> history = auditLogService.getHistory("Record", "123");

        assertThat(history).containsExactly(entry);
    }
}
