package org.rocs.osdrmsa.controller.record;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.dto.request.RecordCreateRequest;
import org.rocs.osdrmsa.dto.request.RecordUpdateRequest;
import org.rocs.osdrmsa.dto.mapper.RecordDtoMapper;
import org.rocs.osdrmsa.domain.department.Department;
import org.rocs.osdrmsa.domain.record.Record;
import org.rocs.osdrmsa.dto.response.AppealResponse;
import org.rocs.osdrmsa.service.record.RecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PREFECT')")
    public ResponseEntity<AppealResponse.RecordResponse> create(@RequestBody RecordCreateRequest request) {
        Record created = recordService.createStudentRecord(RecordDtoMapper.toEntity(request));
        if (created == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(RecordDtoMapper.toResponse(created));
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PREFECT')")
    public ResponseEntity<AppealResponse.RecordResponse> update(@RequestBody RecordUpdateRequest request) {
        Record updated = recordService.updateStudentRecord(RecordDtoMapper.toEntity(request));
        if (updated == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(RecordDtoMapper.toResponse(updated));
    }

    @PatchMapping("/{recordId}/resolve")
    @PreAuthorize("hasAnyRole('ADMIN', 'PREFECT')")
    public ResponseEntity<AppealResponse.RecordResponse> resolve(@PathVariable Long recordId) {
        Record resolved = recordService.resolveRecord(recordId);
        if (resolved == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(RecordDtoMapper.toResponse(resolved));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PREFECT', 'STAFF') "
            + "or (hasRole('USER') and @access.isSelfStudent(#studentId))")
    public ResponseEntity<List<AppealResponse.RecordResponse>> getByStudent(@PathVariable String studentId) {
        return ResponseEntity.ok(
                recordService.getRecordByStudentId(studentId).stream()
                        .map(RecordDtoMapper::toResponse)
                        .toList());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PREFECT', 'PRINCIPAL', 'DSS')")
    public ResponseEntity<List<AppealResponse.RecordResponse>> getByDepartment(
            @RequestParam Department department,
            @RequestParam String schoolYear) {
        return ResponseEntity.ok(
                recordService.getViolationsByDepartment(department, schoolYear).stream()
                        .map(RecordDtoMapper::toResponse)
                        .toList());
    }
}