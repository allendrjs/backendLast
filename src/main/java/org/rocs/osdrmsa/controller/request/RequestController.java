package org.rocs.osdrmsa.controller.request;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.controller.request.dto.RequestDecisionRequest;
import org.rocs.osdrmsa.controller.request.dto.RequestResponse;
import org.rocs.osdrmsa.controller.request.dto.RequestSubmitRequest;
import org.rocs.osdrmsa.controller.request.mapper.RequestDtoMapper;
import org.rocs.osdrmsa.domain.request.Request;
import org.rocs.osdrmsa.domain.request.RequestStatus;
import org.rocs.osdrmsa.service.request.RequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Requests/responses use dedicated DTOs so status/dateProcessed/remarks
 * can't be set by the client on submission, matching the pattern applied
 * to RecordController/AppealController.
 */
@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<RequestResponse> submit(@RequestBody RequestSubmitRequest request) {
        Request submitted = requestService.submitRequest(RequestDtoMapper.toEntity(request));
        return ResponseEntity.ok(RequestDtoMapper.toResponse(submitted));
    }

    @PatchMapping("/{requestId}/decision")
    @PreAuthorize("hasAnyRole('ADMIN', 'PREFECT')")
    public ResponseEntity<RequestResponse> decide(
            @PathVariable Long requestId, @RequestBody RequestDecisionRequest decision) {
        Request processed = requestService.processRequest(requestId, decision.decision(), decision.remarks());
        return ResponseEntity.ok(RequestDtoMapper.toResponse(processed));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PREFECT', 'STAFF')")
    public ResponseEntity<List<RequestResponse>> getByEmployee(@PathVariable String employeeId) {
        return ResponseEntity.ok(
                requestService.getByEmployeeId(employeeId).stream()
                        .map(RequestDtoMapper::toResponse)
                        .toList());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PREFECT')")
    public ResponseEntity<List<RequestResponse>> getAll(
            @RequestParam(required = false) RequestStatus status) {
        if (status != null) {
            return ResponseEntity.ok(
                    requestService.getByStatus(status).stream()
                            .map(RequestDtoMapper::toResponse)
                            .toList());
        }
        return ResponseEntity.ok(
                requestService.getAll().stream()
                        .map(RequestDtoMapper::toResponse)
                        .toList());
    }
}
