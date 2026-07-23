package org.rocs.osdrmsa.controller.appeal;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.controller.appeal.dto.AppealFileRequest;
import org.rocs.osdrmsa.controller.appeal.dto.AppealResponse;
import org.rocs.osdrmsa.controller.appeal.dto.AppealReviewRequest;
import org.rocs.osdrmsa.controller.appeal.mapper.AppealDtoMapper;
import org.rocs.osdrmsa.domain.appeal.Appeal;
import org.rocs.osdrmsa.service.appeal.AppealService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * getByStudent enforces ownership for STUDENT-role callers via
 * OwnAccessEvaluator, same as RecordController. "file" trusts the
 * enrollment/record the client supplies rather than cross-checking it
 * belongs to the caller - that's a separate, not-yet-closed gap.
 * Requests/responses use dedicated DTOs so status/dateFiled/dateProcessed
 * can't be set by the client and aren't tied to the JPA entity shape.
 */
@RestController
@RequestMapping("/api/appeals")
@RequiredArgsConstructor
public class AppealController {

    private final AppealService appealService;

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<AppealResponse> file(@RequestBody AppealFileRequest request) {
        Appeal filed = appealService.fileAppeal(AppealDtoMapper.toEntity(request));
        return ResponseEntity.ok(AppealDtoMapper.toResponse(filed));
    }

    @PatchMapping("/{appealId}/review")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'PREFECT')")
    public ResponseEntity<AppealResponse> review(
            @PathVariable Long appealId, @RequestBody AppealReviewRequest request) {
        Appeal reviewed = appealService.reviewAppeal(appealId, request.status(), request.remarks());
        return ResponseEntity.ok(AppealDtoMapper.toResponse(reviewed));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'PREFECT') "
            + "or (hasRole('STUDENT') and @access.isSelfStudent(#studentId))")
    public ResponseEntity<List<AppealResponse>> getByStudent(@PathVariable String studentId) {
        return ResponseEntity.ok(
                appealService.getByStudentId(studentId).stream()
                        .map(AppealDtoMapper::toResponse)
                        .toList());
    }

    @GetMapping("/record/{recordId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'PREFECT')")
    public ResponseEntity<List<AppealResponse>> getByRecord(@PathVariable Long recordId) {
        return ResponseEntity.ok(
                appealService.getByRecordId(recordId).stream()
                        .map(AppealDtoMapper::toResponse)
                        .toList());
    }
}
