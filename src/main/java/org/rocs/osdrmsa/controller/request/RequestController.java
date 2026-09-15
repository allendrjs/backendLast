package org.rocs.osdrmsa.controller.request;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.rocs.osdrmsa.dto.request.RequestDecisionRequest;
import org.rocs.osdrmsa.dto.response.RequestResponse;
import org.rocs.osdrmsa.dto.request.RequestSubmitRequest;
import org.rocs.osdrmsa.dto.mapper.RequestDtoMapper;
import org.rocs.osdrmsa.domain.request.Request;
import org.rocs.osdrmsa.domain.request.RequestStatus;
import org.rocs.osdrmsa.service.request.RequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<RequestResponse> submit(
            @RequestBody RequestSubmitRequest request,
            Authentication authentication) {

        Request submitted = requestService.submitRequest(
                RequestDtoMapper.toEntity(request),
                authentication.getName()
        );

        return ResponseEntity.ok(RequestDtoMapper.toResponse(submitted));
    }

    @PatchMapping("/{requestId}/decision")
    @PreAuthorize("hasAnyRole('ADMIN', 'PREFECT')")
    public ResponseEntity<RequestResponse> decide(
            @PathVariable Long requestId,
            @RequestBody RequestDecisionRequest decision) {

        Request processed = requestService.processRequest(
                requestId,
                decision.decision(),
                decision.remarks()
        );

        return ResponseEntity.ok(RequestDtoMapper.toResponse(processed));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PREFECT', 'STAFF')")
    public ResponseEntity<List<RequestResponse>> getByEmployee(
            @PathVariable String employeeId) {

        return ResponseEntity.ok(
                requestService.getByEmployeeId(employeeId)
                        .stream()
                        .map(RequestDtoMapper::toResponse)
                        .toList()
        );
    }

    @GetMapping("/my-department")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<List<RequestResponse>> getMyDepartment(
            Authentication authentication) {

        String username = authentication.getName();

        return ResponseEntity.ok(
                requestService.getMyDepartmentRequests(username)
                        .stream()
                        .map(RequestDtoMapper::toResponse)
                        .toList()
        );
    }

    @GetMapping("/my-department/name")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<String> getMyDepartmentName(
            Authentication authentication) {

        String username = authentication.getName();

        return ResponseEntity.ok(
                requestService.getMyDepartmentName(username)
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PREFECT')")
    public ResponseEntity<List<RequestResponse>> getAll(
            @RequestParam(required = false) RequestStatus status) {

        if (status != null) {
            return ResponseEntity.ok(
                    requestService.getByStatus(status)
                            .stream()
                            .map(RequestDtoMapper::toResponse)
                            .toList()
            );
        }

        return ResponseEntity.ok(
                requestService.getAll()
                        .stream()
                        .map(RequestDtoMapper::toResponse)
                        .toList()
        );
    }
}
