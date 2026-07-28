package org.rocs.osdrmsa.service.enrollment.impl;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.enrollment.Enrollment;
import org.rocs.osdrmsa.repository.enrollment.EnrollmentRepository;
import org.rocs.osdrmsa.service.enrollment.EnrollmentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    @Override
    public List<Enrollment> getAllLatestEnrollments() {
        return enrollmentRepository.findAllLatest();
    }

    @Override
    public Optional<Enrollment> getById(Long enrollmentId) {
        return enrollmentRepository.findById(enrollmentId);
    }

    @Override
    public Optional<Enrollment> getLatestByStudentId(String studentId) {
        return enrollmentRepository.findTopByStudentStudentIdOrderBySchoolYearDesc(studentId);
    }

    @Override
    public List<Enrollment> getHistoryByStudentId(String studentId) {
        return enrollmentRepository.findByStudentStudentIdOrderBySchoolYearDesc(studentId);
    }
}
