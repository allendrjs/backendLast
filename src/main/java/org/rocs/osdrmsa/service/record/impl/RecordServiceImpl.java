package org.rocs.osdrmsa.service.record.impl;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.department.Department;
import org.rocs.osdrmsa.domain.disciplinary.action.DisciplinaryAction;
import org.rocs.osdrmsa.domain.record.Record;
import org.rocs.osdrmsa.domain.record.RecordStatus;
import org.rocs.osdrmsa.repository.record.RecordRepository;
import org.rocs.osdrmsa.service.record.OffenseFrequency;
import org.rocs.osdrmsa.service.record.RecordService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecordServiceImpl implements RecordService {

    private final RecordRepository recordRepository;

    @Override
    public List<Record> getByStudentId(String studentId) {
        return recordRepository.findByEnrollmentStudentStudentId(studentId);
    }

    @Override
    public List<Record> getBySchoolYear(String schoolYear) {
        return recordRepository.findByEnrollmentSchoolYearOrderByDateOfViolationDesc(schoolYear);
    }

    @Override
    public List<Record> getByDepartmentAndSchoolYear(Department department, String schoolYear) {
        return recordRepository.findByEnrollmentDepartmentAndEnrollmentSchoolYear(department, schoolYear);
    }

    @Override
    public Optional<Record> getById(Long recordId) {
        return recordRepository.findById(recordId);
    }

    @Override
    public Record create(Record record) {
        record.setRecordId(null);
        if (record.getStatus() == null) {
            record.setStatus(RecordStatus.PENDING);
        }
        return recordRepository.save(record);
    }

    @Override
    public Record resolve(Long recordId, Long actionId, String remarks) {
        Record existing = recordRepository.findById(recordId)
                .orElseThrow(() -> new NoSuchElementException("Record not found: " + recordId));

        DisciplinaryAction action = new DisciplinaryAction();
        action.setActionId(actionId);

        existing.setAction(action);
        existing.setRemarks(remarks);
        existing.setDateOfResolution(LocalDate.now());
        existing.setStatus(RecordStatus.RESOLVED);

        return recordRepository.save(existing);
    }

    @Override
    public long countBySchoolYear(String schoolYear) {
        return recordRepository.countByEnrollmentSchoolYear(schoolYear);
    }

    @Override
    public long countToday() {
        return recordRepository.countByDateOfViolation(LocalDate.now());
    }

    @Override
    public List<OffenseFrequency> getOffenseFrequency(String schoolYear) {
        return recordRepository.findOffenseFrequencyBySchoolYear(schoolYear).stream()
                .map(p -> new OffenseFrequency(p.getOffense(), p.getTotal()))
                .toList();
    }
}
