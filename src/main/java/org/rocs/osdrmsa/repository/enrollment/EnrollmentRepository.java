package org.rocs.osdrmsa.repository.enrollment;

import org.rocs.osdrmsa.domain.enrollment.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    Optional<Enrollment> findByStudentStudentId(String studentId);

    Optional<Enrollment> findTopByStudentStudentIdOrderBySchoolYearDesc(String studentId);

    /**
     * Full enrollment history for a single student, newest school year
     * first.
     */
    List<Enrollment> findByStudentStudentIdOrderBySchoolYearDesc(String studentId);

    /**
     * The latest enrollment row per student, across all students. Plain
     * findAll() would return every enrollment row ever created; this
     * filters down to one row per student (the one with the max school
     * year).
     */
    @Query("SELECT e FROM Enrollment e WHERE e.schoolYear = "
            + "(SELECT MAX(e2.schoolYear) FROM Enrollment e2 WHERE e2.student = e.student)")
    List<Enrollment> findAllLatest();

}
