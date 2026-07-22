package org.rocs.osdrmsa.service.enrollment;

import org.rocs.osdrmsa.domain.enrollment.Enrollment;

import java.util.List;
import java.util.Optional;

public interface EnrollmentService {
    public List<Enrollment> getAllLatestEnrollments();
    public Optional<Enrollment> getEnrollmentsByStudentId(Long id);
    public Enrollment getLatestEnrollmentByStudentId(String studentId);
}
