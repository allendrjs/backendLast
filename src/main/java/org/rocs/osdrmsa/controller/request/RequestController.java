package org.rocs.osdrmsa.controller.request;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.login.Login;
import org.rocs.osdrmsa.domain.person.employee.Employee;
import org.rocs.osdrmsa.domain.request.Request;
import org.rocs.osdrmsa.domain.request.RequestStatus;
import org.rocs.osdrmsa.dto.request.RequestCreateRequest;
import org.rocs.osdrmsa.dto.request.RequestDecisionRequest;
import org.rocs.osdrmsa.dto.response.RequestResponse;
import org.rocs.osdrmsa.service.employee.EmployeeService;
import org.rocs.osdrmsa.service.login.LoginService;
import org.rocs.osdrmsa.service.request.RequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Student record request workflow. A Department Head files a request to
 * search/view a student's record; the Prefect (via Desktop, same shared
 * API) approves or denies it. This is the one place Dept Head touches
 * record data, consistent with their confirmed scope of "search and
 * request" only - they never see RecordController's data directly.
 *
 * RequestStatus only has PENDING/RESOLVED/APPEALED (no separate
 * APPROVED/DENIED state), so both approve and deny land on RESOLVED;
 * the outcome is recorded in remarks (deny remarks are prefixed
 * "Denied: " and required, mirroring AppealController's deny validation).
 */
@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;
    private final EmployeeService employeeService;
    private final LoginService loginService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PREFECT')")
    public List<RequestResponse> getByStatus(@RequestParam RequestStatus status) {
        return requestService.getByStatus(status).stream().map(RequestController::toResponse).toList();
    }

    @GetMapping("/{requestId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PREFECT')")
    public RequestResponse getById(@PathVariable Long requestId) {
        Request request = requestService.getById(requestId)
                .orElseThrow(() -> new NoSuchElementException("Request not found: " + requestId));
        return toResponse(request);
    }

    /** Department Head - the requests they've personally filed, resolved from their own JWT identity. */
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public List<RequestResponse> getOwnRequests(Authentication authentication) {
        Employee employee = resolveCallerEmployee(authentication);
        return requestService.getByEmployeeId(employee.getEmployeeId()).stream()
                .map(RequestController::toResponse).toList();
    }

    /** Department Head files a new record request. */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public ResponseEntity<RequestResponse> create(@RequestBody RequestCreateRequest body, Authentication authentication) {
        Employee employee = resolveCallerEmployee(authentication);

        Request request = new Request();
        request.setEmployee(employee);
        request.setDetails(body.details());
        request.setMessage(body.message());
        request.setType(body.type());

        Request saved = requestService.create(request);
        return ResponseEntity.ok(toResponse(saved));
    }

    @PutMapping("/{requestId}/approve")
    @PreAuthorize("hasAuthority('ROLE_PREFECT')")
    public RequestResponse approve(@PathVariable Long requestId, @RequestBody RequestDecisionRequest body) {
        return toResponse(requestService.approve(requestId, body.remarks()));
    }

    @PutMapping("/{requestId}/deny")
    @PreAuthorize("hasAuthority('ROLE_PREFECT')")
    public RequestResponse deny(@PathVariable Long requestId, @RequestBody RequestDecisionRequest body) {
        return toResponse(requestService.deny(requestId, body.remarks()));
    }

    private Employee resolveCallerEmployee(Authentication authentication) {
        Login login = loginService.getByUsername(authentication.getName())
                .orElseThrow(() -> new NoSuchElementException("Login not found."));
        if (login.getPerson() == null) {
            throw new NoSuchElementException("This account has no linked person record.");
        }
        return employeeService.getByPersonId(login.getPerson().getPersonId())
                .orElseThrow(() -> new NoSuchElementException("No employee record linked to this account."));
    }

    private static RequestResponse toResponse(Request request) {
        Employee employee = request.getEmployee();
        return new RequestResponse(
                request.getRequestId(),
                employee != null ? employee.getEmployeeId() : null,
                employee != null && employee.getPerson() != null
                        ? employee.getPerson().getFirstName() + " " + employee.getPerson().getLastName()
                        : null,
                request.getDetails(),
                request.getMessage(),
                request.getType(),
                request.getStatus(),
                request.getDateProcessed(),
                request.getRemarks()
        );
    }
}
