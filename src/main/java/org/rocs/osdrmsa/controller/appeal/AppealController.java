package org.rocs.osdrmsa.controller.appeal;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.appeal.Appeal;
import org.rocs.osdrmsa.domain.appeal.AppealEditHistory;
import org.rocs.osdrmsa.dto.request.AppealFileRequest;
import org.rocs.osdrmsa.dto.request.AppealRequest;
import org.rocs.osdrmsa.dto.request.AppealUpdateRequest;
import org.rocs.osdrmsa.service.appeal.AppealService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appeals")
@RequiredArgsConstructor
public class AppealController {

    private final AppealService appealService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PREFECT')")
    public List<Appeal> getAppeals(@RequestParam String status) {
        return appealService.getAppealsByStatus(status);
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN','PREFECT','STAFF') "
            + "or (hasRole('USER') and @access.isSelfStudent(#studentId))")
    public List<Appeal> getAppealsForStudent(@PathVariable String studentId, Authentication authentication) {
        List<Appeal> appeals = appealService.getAppealsByStudentId(studentId);
        boolean isStudent = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
        return isStudent ? appeals.stream().map(this::withoutAi).toList() : appeals;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') and @access.canFileAppeal("
            + "#request.recordId(), #request.enrollmentId(), #request.documentId())")
    public ResponseEntity<Appeal> submitAppeal(@RequestBody AppealFileRequest request) {
        Appeal appeal = appealService.submitAppeal(
                request.recordId(), request.enrollmentId(), request.message(), request.documentId());
        return ResponseEntity.ok(withoutAi(appeal));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') and @access.isSelfAppeal(#id)")
    public ResponseEntity<Appeal> updateAppeal(@PathVariable Long id, @RequestBody AppealUpdateRequest request) {
        return ResponseEntity.ok(withoutAi(appealService.updateAppeal(id, request.message())));
    }

    @GetMapping("/{id}/history")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AppealEditHistory>> getEditHistory(@PathVariable Long id) {
        return ResponseEntity.ok(appealService.getEditHistory(id));
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

    private Appeal withoutAi(Appeal appeal) {
        if (appeal == null) {
            return null;
        }
        Appeal copy = new Appeal();
        BeanUtils.copyProperties(appeal, copy);
        copy.setAiRecommendation(null);
        copy.setAiReasoning(null);
        return copy;
    }
}
