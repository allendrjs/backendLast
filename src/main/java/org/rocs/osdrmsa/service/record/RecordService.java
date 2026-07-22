package org.rocs.osdrmsa.service.record;

import org.rocs.osdrmsa.domain.department.Department;
import org.rocs.osdrmsa.domain.record.Record;

import java.util.List;

public interface RecordService {

    Record createStudentRecord(Record record);

    Record updateStudentRecord(Record record);

    Record resolveRecord(Long recordId);

    List<Record> getViolationsByDepartment(
            Department department,
            String schoolYear);

    List<Record> getRecordByStudentId(String studentId);

}
