package org.rocs.osdrmsa.controller.record;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.controller.record.dto.RecordCreateRequest;
import org.rocs.osdrmsa.controller.record.dto.RecordResponse;
import org.rocs.osdrmsa.controller.record.dto.RecordUpdateRequest;
import org.rocs.osdrmsa.controller.record.mapper.RecordDtoMapper;
import org.rocs.osdrmsa.domain.department.Department;
import org.rocs.osdrmsa.domain.record.Record;
import org.rocs.osdrmsa.service.record.RecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Exposes RecordServiceImpl's existing business logic over REST.
 * Requests/responses use dedicated DTOs (not the raw JPA entity) so clients
 * can't over-post fields like recordId/status, and so the OpenAPI contract
 * published to the web/mobile teams stays stable independent of entity
 * changes. getByStudent enforces ownership for STUDENT-role callers via
 * OwnAccessEvaluator; department/employee-facing endpoints remain
 * role-level only since Admin/Prefect/Department Head access is already
 * scoped by their role, not by identity.
 */
@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PREFECT')")
    public ResponseEntity<RecordResponse> create(@RequestBody RecordCreateRequest request) {
        Record created = recordService.createStudentRecord(RecordDtoMapper.toEntity(request));
        if (created == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(RecordDtoMapper.toResponse(created));
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PREFECT')")
    public ResponseEntity<RecordResponse> update(@RequestBody RecordUpdateRequest request) {
        Record updated = recordService.updateStudentRecord(RecordDtoMapper.toEntity(request));
        if (updated == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(RecordDtoMapper.toResponse(updated));
    }

    @PatchMapping("/{recordId}/resolve")
    @PreAuthorize("hasAnyRole('ADMIN', 'PREFECT')")
    public ResponseEntity<RecordResponse> resolve(@PathVariable Long recordId) {
        Record resolved = recordService.resolveRecord(recordId);
        if (resolved == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(RecordDtoMapper.toResponse(resolved));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PREFECT', 'STAFF') "
            + "or (hasRole('USER') and @access.isSelfStudent(#studentId))")
    public ResponseEntity<List<RecordResponse>> getByStudent(@PathVariable String studentId) {
        return ResponseEntity.ok(
                recordService.getRecordByStudentId(studentId).stream()
                        .map(RecordDtoMapper::toResponse)
                        .toList());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PREFECT')")
    public ResponseEntity<List<RecordResponse>> getByDepartment(
            @RequestParam Department department,
            @RequestParam String schoolYear) {
        return ResponseEntity.ok(
                recordService.getViolationsByDepartment(department, schoolYear).stream()
                        .map(RecordDtoMapper::toResponse)
                        .toList());
    }
}
