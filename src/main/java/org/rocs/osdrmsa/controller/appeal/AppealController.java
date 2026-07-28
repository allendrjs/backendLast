package org.rocs.osdrmsa.controller.appeal;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.appeal.Appeal;
import org.rocs.osdrmsa.domain.person.Person;
import org.rocs.osdrmsa.domain.person.student.Student;
import org.rocs.osdrmsa.dto.request.AppealRequest;
import org.rocs.osdrmsa.dto.response.AppealResponse;
import org.rocs.osdrmsa.service.appeal.AppealService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Appeal review. Listing is visible to Admin and Prefect (oversight);
 * approve/deny is Prefect-only - the Prefect makes the disciplinary call
 * through their Desktop client, which authenticates against this same
 * shared API. Previously this controller (feature/OSDA-573) had zero role
 * restrictions - anyone authenticated could approve or deny an appeal.
 */
@RestController
@RequestMapping("/api/appeals")
@RequiredArgsConstructor
public class AppealController {

    private final AppealService appealService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PREFECT')")
    public List<AppealResponse> getAppeals(@RequestParam String status) {
        return appealService.getAppealsByStatus(status).stream()
                .map(AppealController::toResponse)
                .toList();
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('ROLE_PREFECT')")
    public ResponseEntity<Void> approve(@PathVariable Long id, @RequestBody AppealRequest request) {
        appealService.approveAppeal(id, request.remarks());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/deny")
    @PreAuthorize("hasAuthority('ROLE_PREFECT')")
    public ResponseEntity<Void> deny(@PathVariable Long id, @RequestBody AppealRequest request) {
        appealService.denyAppeal(id, request.remarks());
        return ResponseEntity.ok().build();
    }

    private static AppealResponse toResponse(Appeal appeal) {
        Student student = appeal.getEnrollment() != null ? appeal.getEnrollment().getStudent() : null;
        Person person = student != null ? student.getPerson() : null;
        String studentName = person != null
                ? (person.getFirstName() + " " + person.getLastName())
                : null;
        String offenseName = appeal.getRecord() != null && appeal.getRecord().getOffense() != null
                ? appeal.getRecord().getOffense().getOffense()
                : null;

        return new AppealResponse(
                appeal.getAppealId(),
                student != null ? student.getStudentId() : null,
                studentName,
                offenseName,
                appeal.getMessage(),
                appeal.getStatus(),
                appeal.getRemarks(),
                appeal.getDateFiled(),
                appeal.getDateProcessed()
        );
    }
}
