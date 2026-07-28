package org.rocs.osdrmsa.repository.record;

import org.rocs.osdrmsa.domain.department.Department;
import org.rocs.osdrmsa.domain.record.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {

    List<Record> findByEnrollmentStudentStudentId(String studentId);

    List<Record> findByEnrollmentDepartmentAndEnrollmentSchoolYear(Department department, String schoolYear);

    /**
     * All records for a school year regardless of department. Backs
     * dashboard views where the caller isn't scoped to one department.
     */
    List<Record> findByEnrollmentSchoolYearOrderByDateOfViolationDesc(String schoolYear);

    /** Total violation count for a school year. */
    long countByEnrollmentSchoolYear(String schoolYear);

    /** Violation count for a single day, used for "today's violations". */
    long countByDateOfViolation(LocalDate dateOfViolation);

    /**
     * Offense names ranked by how often they appear in a school year's
     * records, most frequent first. Backs the "most frequent offenses"
     * dashboard stat.
     */
    @Query("SELECT r.offense.offense AS offense, COUNT(r) AS total "
            + "FROM Record r WHERE r.enrollment.schoolYear = :schoolYear "
            + "GROUP BY r.offense.offense ORDER BY COUNT(r) DESC")
    List<OffenseFrequencyProjection> findOffenseFrequencyBySchoolYear(@Param("schoolYear") String schoolYear);

}
