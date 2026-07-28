package org.rocs.osdrmsa.controller.guardian;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.person.Person;
import org.rocs.osdrmsa.domain.person.guardian.Guardian;
import org.rocs.osdrmsa.dto.response.GuardianResponse;
import org.rocs.osdrmsa.service.guardian.GuardianService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Guardian contact lookup, keyed off a student. Read-only - GuardianService
 * only supports lookup, no create/update/delete yet. Staff-only: guardian
 * contact info (used for SMS notification per the Web thesis) isn't
 * something a student needs to read back through this API.
 */
@RestController
@RequestMapping("/api/guardians")
@RequiredArgsConstructor
public class GuardianController {

    private final GuardianService guardianService;

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PREFECT','ROLE_STAFF')")
    public List<GuardianResponse> getByStudentId(@PathVariable String studentId) {
        return guardianService.getByStudentId(studentId).stream()
                .map(GuardianController::toResponse).toList();
    }

    private static GuardianResponse toResponse(Guardian guardian) {
        Person person = guardian.getPerson();
        return new GuardianResponse(
                guardian.getGuardianId(),
                person != null ? person.getPersonId() : null,
                person != null ? person.getFirstName() : null,
                person != null ? person.getLastName() : null,
                guardian.getContactNumber(),
                guardian.getRelationship()
        );
    }
}
