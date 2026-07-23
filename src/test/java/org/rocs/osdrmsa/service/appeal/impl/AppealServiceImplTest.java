package org.rocs.osdrmsa.service.appeal.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rocs.osdrmsa.domain.appeal.Appeal;
import org.rocs.osdrmsa.domain.appeal.AppealStatus;
import org.rocs.osdrmsa.domain.enrollment.Enrollment;
import org.rocs.osdrmsa.domain.record.Record;
import org.rocs.osdrmsa.repository.appeal.AppealRepository;
import org.rocs.osdrmsa.service.audit.AuditLogService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppealServiceImplTest {

    @Mock
    private AppealRepository appealRepository;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AppealServiceImpl appealService;

    private Appeal filedAppeal;

    @BeforeEach
    void setUp() {
        filedAppeal = new Appeal();
        filedAppeal.setAppealID(1L);
        filedAppeal.setRecord(new Record());
        filedAppeal.setEnrollment(new Enrollment());
        filedAppeal.setMessage("I was not involved in this incident.");
        filedAppeal.setStatus(AppealStatus.PENDING);
    }

    @Test
    void fileAppeal_rejectsMissingRecordOrEnrollment() {
        Appeal appeal = new Appeal();
        appeal.setMessage("some message");

        assertThatThrownBy(() -> appealService.fileAppeal(appeal))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void fileAppeal_rejectsBlankMessage() {
        Appeal appeal = new Appeal();
        appeal.setRecord(new Record());
        appeal.setEnrollment(new Enrollment());
        appeal.setMessage("   ");

        assertThatThrownBy(() -> appealService.fileAppeal(appeal))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void fileAppeal_setsStatusToFiledAndStampsDate() {
        Appeal appeal = new Appeal();
        appeal.setRecord(new Record());
        appeal.setEnrollment(new Enrollment());
        appeal.setMessage("Requesting reconsideration.");

        when(appealRepository.save(any(Appeal.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Appeal saved = appealService.fileAppeal(appeal);

        assertThat(saved.getStatus()).isEqualTo(AppealStatus.PENDING);
        assertThat(saved.getDateFiled()).isNotNull();
        assertThat(saved.getDateProcessed()).isNull();
    }

    @Test
    void reviewAppeal_allowsFiledToUnderReview() {
        when(appealRepository.findById(1L)).thenReturn(Optional.of(filedAppeal));
        when(appealRepository.save(any(Appeal.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Appeal result = appealService.reviewAppeal(1L, AppealStatus.UNDER_REVIEW, null);

        assertThat(result.getStatus()).isEqualTo(AppealStatus.UNDER_REVIEW);
        assertThat(result.getDateProcessed()).isNull();
    }

    @Test
    void reviewAppeal_allowsDirectApprovalFromPending() {
        when(appealRepository.findById(1L)).thenReturn(Optional.of(filedAppeal));
        when(appealRepository.save(any(Appeal.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Appeal result = appealService.reviewAppeal(1L, AppealStatus.APPROVED, "Valid excuse.");

        assertThat(result.getStatus()).isEqualTo(AppealStatus.APPROVED);
        assertThat(result.getDateProcessed()).isNotNull();
    }

    @Test
    void reviewAppeal_allowsDirectDenialFromPending() {
        when(appealRepository.findById(1L)).thenReturn(Optional.of(filedAppeal));
        when(appealRepository.save(any(Appeal.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Appeal result = appealService.reviewAppeal(1L, AppealStatus.DENIED, "Not sufficient.");

        assertThat(result.getStatus()).isEqualTo(AppealStatus.DENIED);
        assertThat(result.getDateProcessed()).isNotNull();
    }

    @Test
    void reviewAppeal_stampsDateProcessedOnApproval() {
        filedAppeal.setStatus(AppealStatus.UNDER_REVIEW);
        when(appealRepository.findById(1L)).thenReturn(Optional.of(filedAppeal));
        when(appealRepository.save(any(Appeal.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Appeal result = appealService.reviewAppeal(1L, AppealStatus.APPROVED, "Valid excuse.");

        assertThat(result.getStatus()).isEqualTo(AppealStatus.APPROVED);
        assertThat(result.getDateProcessed()).isNotNull();
        assertThat(result.getRemarks()).isEqualTo("Valid excuse.");
    }

    @Test
    void reviewAppeal_rejectsActingOnTerminalState() {
        filedAppeal.setStatus(AppealStatus.APPROVED);
        when(appealRepository.findById(1L)).thenReturn(Optional.of(filedAppeal));

        assertThatThrownBy(() -> appealService.reviewAppeal(1L, AppealStatus.DENIED, "too late"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
