package org.rocs.osdrmsa.service.enrollment.impl;

import org.rocs.osdrmsa.domain.enrollment.Enrollment;
import org.rocs.osdrmsa.repository.enrollment.EnrollmentRepository;
import org.rocs.osdrmsa.service.enrollment.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    @Autowired
    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public List<Enrollment> getAllLatestEnrollments() {
        return enrollmentRepository.findAll();
    }

    @Override
    public Optional<Enrollment> getEnrollmentsByStudentId(Long id) {
        return enrollmentRepository.findById(id);
    }

    @Override
    public Enrollment getLatestEnrollmentByStudentId(String studentId) {
        return enrollmentRepository.findLatestByStudentIdOrderBySchoolYearDesc(studentId).orElse(null);
    }

}
