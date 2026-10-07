package org.rocs.osdrmsa.service.appeal.impl;

import org.rocs.osdrmsa.domain.academicperiod.AcademicPeriod;
import org.rocs.osdrmsa.domain.appeal.Appeal;
import org.rocs.osdrmsa.domain.appeal.AppealEditHistory;
import org.rocs.osdrmsa.domain.document.Document;
import org.rocs.osdrmsa.domain.enrollment.Enrollment;
import org.rocs.osdrmsa.domain.record.Record;
import org.rocs.osdrmsa.domain.record.RecordStatus;
import org.rocs.osdrmsa.repository.academicperiod.AcademicPeriodRepository;
import org.rocs.osdrmsa.repository.appeal.AppealEditHistoryRepository;
import org.rocs.osdrmsa.repository.appeal.AppealRepository;
import org.rocs.osdrmsa.repository.document.DocumentRepository;
import org.rocs.osdrmsa.repository.enrollment.EnrollmentRepository;
import org.rocs.osdrmsa.repository.record.RecordRepository;
import org.rocs.osdrmsa.service.appeal.AppealAiProcessor;
import org.rocs.osdrmsa.service.appeal.AppealService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AppealServiceImpl implements AppealService {

    private static final Logger log = LoggerFactory.getLogger(AppealServiceImpl.class);

    private final AppealRepository appealRepository;
    private final AppealEditHistoryRepository appealEditHistoryRepository;
    private final AcademicPeriodRepository academicPeriodRepository;
    private final RecordRepository recordRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final DocumentRepository documentRepository;
    private final AppealAiProcessor appealAiProcessor;

    public AppealServiceImpl(
            AppealRepository appealRepository,
            AppealEditHistoryRepository appealEditHistoryRepository,
            AcademicPeriodRepository academicPeriodRepository,
            RecordRepository recordRepository,
            EnrollmentRepository enrollmentRepository,
            DocumentRepository documentRepository,
            AppealAiProcessor appealAiProcessor) {
        this.appealRepository = appealRepository;
        this.appealEditHistoryRepository = appealEditHistoryRepository;
        this.academicPeriodRepository = academicPeriodRepository;
        this.recordRepository = recordRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.documentRepository = documentRepository;
        this.appealAiProcessor = appealAiProcessor;
    }

    @Override
    public List<Appeal> getAppealsByStatus(String status) {
        return appealRepository.findByStatus(status);
    }

    @Override
    public List<Appeal> getAppealsByStudentId(String studentId) {
        return appealRepository.findByEnrollmentStudentStudentId(studentId);
    }

    @Override
    public Appeal submitAppeal(Long recordId, Long enrollmentId, String message, Long documentId) {
        Record record = recordRepository.findById(recordId)
                .orElseThrow(() -> new NoSuchElementException("Record not found."));
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new NoSuchElementException("Enrollment not found."));

        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Appeal message is required.");
        }

        if (record.getEnrollment() == null
                || record.getEnrollment().getEnrollmentId() == null
                || !record.getEnrollment().getEnrollmentId().equals(enrollmentId)) {
            throw new IllegalArgumentException("This offense does not belong to the given enrollment.");
        }

        if (record.getStatus() != null && record.getStatus() != RecordStatus.PENDING) {
            throw new IllegalStateException("Only offenses that are still pending can be appealed.");
        }

        boolean hasUnapprovedAppeal = appealRepository.findByRecord_RecordId(recordId).stream()
                .anyMatch(existing -> !"APPROVED".equalsIgnoreCase(existing.getStatus()));
        if (hasUnapprovedAppeal) {
            throw new IllegalStateException(
                    "An appeal for this record already exists and has not been approved. "
                            + "If it was denied, please visit the Office of Student Discipline in person."
            );
        }

        academicPeriodRepository.findByActiveTrue().ifPresent(period -> {
            if (period.getAppealDeadline() != null && LocalDate.now().isAfter(period.getAppealDeadline())) {
                throw new IllegalStateException(
                        "The appeal window for " + period.getLabel()
                                + " has closed. Please visit the Office of Student Discipline in person."
                );
            }
        });

        Appeal appeal = new Appeal();
        appeal.setRecord(record);
        appeal.setEnrollment(enrollment);
        appeal.setMessage(message);
        appeal.setDateFiled(LocalDateTime.now());
        appeal.setStatus("PENDING");

        if (documentId != null) {
            documentRepository.findById(documentId).ifPresent(appeal::setDocument);
        }

        Appeal saved = appealRepository.save(appeal);

        record.setStatus(RecordStatus.PROCESSING);
        recordRepository.save(record);

        appealAiProcessor.process(saved.getAppealId());

        return saved;
    }

    @Override
    public Appeal updateAppeal(Long appealId, String newMessage) {
        if (newMessage == null || newMessage.trim().isEmpty()) {
            throw new IllegalArgumentException("Appeal message is required.");
        }

        Appeal appeal = appealRepository.findById(appealId)
                .orElseThrow(() -> new NoSuchElementException("Appeal not found."));

        if (!"PENDING".equalsIgnoreCase(appeal.getStatus()) || appeal.getDateProcessed() != null) {
            throw new IllegalStateException("This appeal can no longer be edited.");
        }

        String oldMessage = appeal.getMessage();
        appeal.setMessage(newMessage.trim());
        appeal.setEdited(true);
        appeal.setEditedAt(LocalDateTime.now());

        Appeal saved = appealRepository.save(appeal);

        AppealEditHistory history = new AppealEditHistory();
        history.setAppeal(saved);
        history.setOldMessage(oldMessage);
        history.setNewMessage(saved.getMessage());
        history.setEditedAt(LocalDateTime.now());
        appealEditHistoryRepository.save(history);

        return saved;
    }

    @Override
    public List<AppealEditHistory> getEditHistory(Long appealId) {
        return appealEditHistoryRepository.findByAppeal_AppealIdOrderByEditedAtAsc(appealId);
    }

    @Override
    public void approveAppeal(Long appealId, String remarks) {
        Appeal appeal = appealRepository.findById(appealId).orElseThrow(() -> new RuntimeException("Appeal not found."));
        requirePending(appeal);

        appeal.setStatus("APPROVED");
        appeal.setRemarks(remarks);
        appeal.setDateProcessed(LocalDateTime.now());

        appealRepository.save(appeal);

        Record record = appeal.getRecord();
        if (record != null) {
            record.setStatus(RecordStatus.APPROVED);
            recordRepository.save(record);
        }
    }

    @Override
    public void denyAppeal(Long appealId, String remarks) {
        if (remarks == null || remarks.trim().isEmpty()) {
            throw new IllegalArgumentException("Denial remarks are required.");
        }

        Appeal appeal = appealRepository.findById(appealId).orElseThrow(() -> new RuntimeException("Appeal not found."));
        requirePending(appeal);

        appeal.setStatus("DENIED");
        appeal.setRemarks(remarks);
        appeal.setDateProcessed(LocalDateTime.now());

        appealRepository.save(appeal);

        Record record = appeal.getRecord();
        if (record != null) {
            record.setStatus(RecordStatus.RESOLVED);
            record.setDateOfResolution(LocalDateTime.now());
            recordRepository.save(record);
        }
    }

    private void requirePending(Appeal appeal) {
        if (!"PENDING".equalsIgnoreCase(appeal.getStatus())) {
            throw new IllegalStateException("This appeal has already been processed.");
        }
    }
}
