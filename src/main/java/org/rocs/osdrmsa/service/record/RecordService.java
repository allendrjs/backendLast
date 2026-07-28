package org.rocs.osdrmsa.service.record;

import org.rocs.osdrmsa.domain.department.Department;
import org.rocs.osdrmsa.domain.record.Record;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RecordService {

    List<Record> getByStudentId(String studentId);

    List<Record> getBySchoolYear(String schoolYear);

    List<Record> getByDepartmentAndSchoolYear(Department department, String schoolYear);

    Optional<Record> getById(Long recordId);

    /** Creates a new violation record, status defaults to PENDING. */
    Record create(Record record);

    /** Sets the sanction and resolution date, moves status to RESOLVED. */
    Record resolve(Long recordId, Long actionId, String remarks);

    long countBySchoolYear(String schoolYear);

    long countToday();

    List<OffenseFrequency> getOffenseFrequency(String schoolYear);
}
