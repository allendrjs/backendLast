package org.rocs.osdrmsa.controller.guardian;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.person.guardian.Guardian;
import org.rocs.osdrmsa.service.guardian.GuardianService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Read-only for now: matches the desktop app's existing guardian lookup
 * (view guardians of a given student). Same access pattern as
 * Enrollment/Record/Appeal's student-scoped endpoints - staff roles can
 * look up any student, a STUDENT-role caller can only look up themselves.
 */
@RestController
@RequestMapping("/api/guardians")
@RequiredArgsConstructor
public class GuardianController {

    private final GuardianService guardianService;

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PREFECT', 'STAFF') "
            + "or (hasRole('USER') and @access.isSelfStudent(#studentId))")
    public ResponseEntity<List<Guardian>> getByStudent(@PathVariable String studentId) {
        return ResponseEntity.ok(guardianService.getByStudentId(studentId));
    }
}
