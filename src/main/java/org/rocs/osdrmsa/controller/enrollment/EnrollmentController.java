package org.rocs.osdrmsa.controller.enrollment;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.enrollment.Enrollment;
import org.rocs.osdrmsa.domain.login.Login;
import org.rocs.osdrmsa.dto.response.EnrollmentResponse;
import org.rocs.osdrmsa.service.enrollment.EnrollmentService;
import org.rocs.osdrmsa.service.login.LoginService;
import org.rocs.osdrmsa.service.student.StudentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Enrollment/disciplinary-status lookups. Read-only - EnrollmentService has
 * no create/update/delete, enrollment records are presumably created as
 * part of a broader enroll-a-student workflow not built yet, not directly
 * through this controller.
 */
@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final StudentService studentService;
    private final LoginService loginService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PREFECT','ROLE_STAFF')")
    public List<EnrollmentResponse> getAllLatest() {
        return enrollmentService.getAllLatestEnrollments().stream()
                .map(EnrollmentController::toResponse).toList();
    }

    @GetMapping("/{enrollmentId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PREFECT','ROLE_STAFF')")
    public EnrollmentResponse getById(@PathVariable Long enrollmentId) {
        return enrollmentService.getById(enrollmentId).map(EnrollmentController::toResponse)
                .orElseThrow(() -> new NoSuchElementException("Enrollment not found: " + enrollmentId));
    }

    @GetMapping("/student/{studentId}/latest")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PREFECT','ROLE_STAFF')")
    public EnrollmentResponse getLatestByStudent(@PathVariable String studentId) {
        return enrollmentService.getLatestByStudentId(studentId).map(EnrollmentController::toResponse)
                .orElseThrow(() -> new NoSuchElementException("No enrollment found for student: " + studentId));
    }

    @GetMapping("/student/{studentId}/history")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PREFECT','ROLE_STAFF')")
    public List<EnrollmentResponse> getHistoryByStudent(@PathVariable String studentId) {
        return enrollmentService.getHistoryByStudentId(studentId).stream()
                .map(EnrollmentController::toResponse).toList();
    }

    /** Student-facing: the caller's own current enrollment/standing, resolved from their own JWT identity. */
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public EnrollmentResponse getOwnLatest(Authentication authentication) {
        Login login = loginService.getByUsername(authentication.getName())
                .orElseThrow(() -> new NoSuchElementException("Login not found."));
        if (login.getPerson() == null) {
            throw new NoSuchElementException("This account has no linked person record.");
        }
        String studentId = studentService.getByPersonId(login.getPerson().getPersonId())
                .orElseThrow(() -> new NoSuchElementException("No student record linked to this account."))
                .getStudentId();

        return enrollmentService.getLatestByStudentId(studentId).map(EnrollmentController::toResponse)
                .orElseThrow(() -> new NoSuchElementException("No enrollment found for this account."));
    }

    private static EnrollmentResponse toResponse(Enrollment enrollment) {
        return new EnrollmentResponse(
                enrollment.getEnrollmentId(),
                enrollment.getStudent() != null ? enrollment.getStudent().getStudentId() : null,
                enrollment.getSchoolYear(),
                enrollment.getStudentLevel(),
                enrollment.getSection(),
                enrollment.getDepartment(),
                enrollment.getDisciplinaryStatus() != null ? enrollment.getDisciplinaryStatus().getDisciplinaryStatusId() : null,
                enrollment.getDisciplinaryStatus() != null ? enrollment.getDisciplinaryStatus().getStatus() : null
        );
    }
}
