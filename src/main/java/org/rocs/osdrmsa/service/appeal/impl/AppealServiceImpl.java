package org.rocs.osdrmsa.service.appeal.impl;

import org.rocs.osdrmsa.domain.appeal.Appeal;
import org.rocs.osdrmsa.domain.enrollment.Enrollment;
import org.rocs.osdrmsa.domain.record.Record;
import org.rocs.osdrmsa.domain.record.RecordStatus;
import org.rocs.osdrmsa.repository.appeal.AppealRepository;
import org.rocs.osdrmsa.repository.enrollment.EnrollmentRepository;
import org.rocs.osdrmsa.repository.record.RecordRepository;
import org.rocs.osdrmsa.service.appeal.AppealService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AppealServiceImpl implements AppealService {

    private final AppealRepository appealRepository;
    private final RecordRepository recordRepository;
    private final EnrollmentRepository enrollmentRepository;

    public AppealServiceImpl(AppealRepository appealRepository, RecordRepository recordRepository, EnrollmentRepository enrollmentRepository) {
        this.appealRepository = appealRepository;
        this.recordRepository = recordRepository;
        this.enrollmentRepository = enrollmentRepository;
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
    public Appeal submitAppeal(Long recordId, Long enrollmentId, String message) {
        Record record = recordRepository.findById(recordId)
                .orElseThrow(() -> new NoSuchElementException("Record not found."));
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new NoSuchElementException("Enrollment not found."));

        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Appeal message is required.");
        }

        Appeal appeal = new Appeal();
        appeal.setRecord(record);
        appeal.setEnrollment(enrollment);
        appeal.setMessage(message);
        appeal.setDateFiled(LocalDate.now());
        appeal.setStatus("PENDING");

        Appeal saved = appealRepository.save(appeal);

        record.setStatus(RecordStatus.PROCESSING);
        recordRepository.save(record);

        return saved;
    }

    @Override
    public void approveAppeal(Long appealId, String remarks) {
        Appeal appeal = appealRepository.findById(appealId).orElseThrow(() -> new RuntimeException("Appeal not found."));

        appeal.setStatus("APPROVED");
        appeal.setRemarks(remarks);
        appeal.setDateProcessed(LocalDate.now());

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

        appeal.setStatus("DENIED");
        appeal.setRemarks(remarks);
        appeal.setDateProcessed(LocalDate.now());

        appealRepository.save(appeal);

        Record record = appeal.getRecord();
        if (record != null) {
            record.setStatus(RecordStatus.RESOLVED);
            record.setDateOfResolution(LocalDate.now());
            recordRepository.save(record);
        }
    }
}