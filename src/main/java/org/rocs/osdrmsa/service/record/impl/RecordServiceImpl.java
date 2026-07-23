package org.rocs.osdrmsa.service.record.impl;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.department.Department;
import org.rocs.osdrmsa.domain.record.RecordStatus;
import org.rocs.osdrmsa.domain.record.Record;
import org.rocs.osdrmsa.repository.record.RecordRepository;
import org.rocs.osdrmsa.service.audit.AuditLogService;
import org.rocs.osdrmsa.service.record.RecordService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class RecordServiceImpl implements RecordService {

    private static final String ENTITY_TYPE = "Record";

    private final RecordRepository recordRepository;
    private final AuditLogService auditLogService;

    @Override
    public Record createStudentRecord(Record record) {

        if (record == null
                || record.getEmployee() == null
                || record.getEmployee().getEmployeeId() == null
                || record.getDateOfViolation() == null) {
            return null;
        }

        if (record.getRemarks() != null
                && record.getRemarks().length() > 500) {
            return null;
        }

        // Force insert rather than merge: a client-supplied non-zero
        // recordId would otherwise cause Spring Data to treat this as an
        // update and silently overwrite an existing record (GenerationType
        // .IDENTITY + non-zero id => isNew() returns false => merge()).
        record.setRecordId(0);
        record.setStatus(RecordStatus.PENDING);

        Record saved = recordRepository.save(record);
        auditLogService.log("RECORD_CREATED", ENTITY_TYPE, String.valueOf(saved.getRecordId()), null);
        return saved;
    }

    @Override
    public Record updateStudentRecord(Record record) {

        if (record == null
                || record.getRecordId() <= 0
                || record.getEmployee() == null
                || record.getEmployee().getEmployeeId() == null
                || record.getDateOfViolation() == null) {
            return null;
        }

        if (record.getRemarks() != null
                && record.getRemarks().length() > 500) {
            return null;
        }

        if (!recordRepository.existsById(record.getRecordId())) {
            throw new NoSuchElementException("Record not found: " + record.getRecordId());
        }

        Record saved = recordRepository.save(record);
        auditLogService.log("RECORD_UPDATED", ENTITY_TYPE, String.valueOf(saved.getRecordId()), null);
        return saved;
    }

    @Override
    public Record resolveRecord(Long recordId) {

        Record record = recordRepository.findById(recordId)
                .orElse(null);

        if (record == null) {
            return null;
        }

        record.setStatus(RecordStatus.RESOLVED);
        record.setDateOfResolution(new java.util.Date());

        Record saved = recordRepository.save(record);
        auditLogService.log("RECORD_RESOLVED", ENTITY_TYPE, String.valueOf(saved.getRecordId()), null);
        return saved;
    }

    @Override
    public List<Record> getRecordByStudentId(String studentId) {
        return recordRepository.findByEnrollmentStudentStudentId(studentId);
    }


    @Override
    public List<Record> getViolationsByDepartment(
            Department department,
            String schoolYear) {

        return recordRepository
                .findByEnrollmentDepartmentAndEnrollmentSchoolYear(
                        department,
                        schoolYear);
    }

}
