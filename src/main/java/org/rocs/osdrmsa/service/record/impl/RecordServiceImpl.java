package org.rocs.osdrmsa.service.record.impl;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.department.Department;
import org.rocs.osdrmsa.domain.record.RecordStatus;
import org.rocs.osdrmsa.domain.record.Record;
import org.rocs.osdrmsa.repository.record.RecordRepository;
import org.rocs.osdrmsa.service.record.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecordServiceImpl implements RecordService {

    private final RecordRepository recordRepository;

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

        record.setStatus(RecordStatus.PENDING);

        return recordRepository.save(record);
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

        return recordRepository.save(record);
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

        return recordRepository.save(record);
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
