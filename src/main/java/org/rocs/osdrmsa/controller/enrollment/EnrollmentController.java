package org.rocs.osdrmsa.controller.enrollment;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.enrollment.Enrollment;
import org.rocs.osdrmsa.service.enrollment.EnrollmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PREFECT')")
    public ResponseEntity<List<Enrollment>> getAllLatest() {
        return ResponseEntity.ok(enrollmentService.getAllLatestEnrollments());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PREFECT')")
    public ResponseEntity<Enrollment> getById(@PathVariable Long id) {
        return enrollmentService.getEnrollmentsByStudentId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/student/{studentId}/latest")
    @PreAuthorize("hasAnyRole('ADMIN', 'PREFECT', 'STAFF') "
            + "or (hasRole('USER') and @access.isSelfStudent(#studentId))")
    public ResponseEntity<Enrollment> getLatestByStudentId(@PathVariable String studentId) {
        Enrollment enrollment = enrollmentService.getLatestEnrollmentByStudentId(studentId);
        if (enrollment == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(enrollment);
    }
}
