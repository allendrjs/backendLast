package org.rocs.osdrmsa.service.enrollment;

import org.rocs.osdrmsa.domain.enrollment.Enrollment;

import java.util.List;
import java.util.Optional;

public interface EnrollmentService {

    /** The latest enrollment row per student, across all students. */
    List<Enrollment> getAllLatestEnrollments();

    Optional<Enrollment> getById(Long enrollmentId);

    Optional<Enrollment> getLatestByStudentId(String studentId);

    /** Full enrollment history for a single student, newest school year first. */
    List<Enrollment> getHistoryByStudentId(String studentId);
}
