package org.rocs.osdrmsa.repository.record;

import org.rocs.osdrmsa.domain.department.Department;
import org.rocs.osdrmsa.domain.record.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {

    List<Record> findByEnrollmentStudentStudentId(String studentId);

    List<Record> findByEnrollmentDepartmentAndEnrollmentSchoolYear(
            Department department,
            String schoolYear);

}
