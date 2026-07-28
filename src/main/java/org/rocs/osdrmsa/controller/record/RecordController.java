package org.rocs.osdrmsa.controller.record;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.department.Department;
import org.rocs.osdrmsa.domain.disciplinary.action.DisciplinaryAction;
import org.rocs.osdrmsa.domain.enrollment.Enrollment;
import org.rocs.osdrmsa.domain.login.Login;
import org.rocs.osdrmsa.domain.offense.Offense;
import org.rocs.osdrmsa.domain.person.employee.Employee;
import org.rocs.osdrmsa.domain.record.Record;
import org.rocs.osdrmsa.dto.request.RecordRequest;
import org.rocs.osdrmsa.dto.request.RecordResolveRequest;
import org.rocs.osdrmsa.dto.response.RecordResponse;
import org.rocs.osdrmsa.dto.response.RecordStatsResponse;
import org.rocs.osdrmsa.service.login.LoginService;
import org.rocs.osdrmsa.service.record.RecordService;
import org.rocs.osdrmsa.service.student.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Violation record management - the core of the disciplinary system.
 *
 * Records are filed and resolved by the Prefect through the Desktop app,
 * which calls this same shared API. Admin can view everything for
 * oversight. Department Head's confirmed scope is "search and request
 * student record" only, not free browsing of violation records - so read
 * access here is deliberately Admin + Prefect only, not Staff. Dept Head
 * reaches record data through the Request approval workflow instead
 * (see RequestController).
 */
@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;
    private final StudentService studentService;
    private final LoginService loginService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PREFECT')")
    public List<RecordResponse> getBySchoolYear(@RequestParam String schoolYear,
                                                 @RequestParam(required = false) Department department) {
        List<Record> records = department != null
                ? recordService.getByDepartmentAndSchoolYear(department, schoolYear)
                : recordService.getBySchoolYear(schoolYear);
        return records.stream().map(RecordController::toResponse).toList();
    }

    @GetMapping("/{recordId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PREFECT')")
    public RecordResponse getById(@PathVariable Long recordId) {
        Record record = recordService.getById(recordId)
                .orElseThrow(() -> new NoSuchElementException("Record not found: " + recordId));
        return toResponse(record);
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PREFECT')")
    public List<RecordResponse> getByStudent(@PathVariable String studentId) {
        return recordService.getByStudentId(studentId).stream().map(RecordController::toResponse).toList();
    }

    /** Student-facing: the caller's own violation records, resolved from their own JWT identity. */
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public List<RecordResponse> getOwnRecords(Authentication authentication) {
        Login login = loginService.getByUsername(authentication.getName())
                .orElseThrow(() -> new NoSuchElementException("Login not found."));
        if (login.getPerson() == null) {
            throw new NoSuchElementException("This account has no linked person record.");
        }
        String studentId = studentService.getByPersonId(login.getPerson().getPersonId())
                .orElseThrow(() -> new NoSuchElementException("No student record linked to this account."))
                .getStudentId();

        return recordService.getByStudentId(studentId).stream().map(RecordController::toResponse).toList();
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PREFECT')")
    public RecordStatsResponse getStats(@RequestParam String schoolYear) {
        return new RecordStatsResponse(
                schoolYear,
                recordService.countBySchoolYear(schoolYear),
                recordService.countToday(),
                recordService.getOffenseFrequency(schoolYear)
        );
    }

    /** Prefect files a new violation record via Desktop. */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_PREFECT')")
    public ResponseEntity<RecordResponse> create(@RequestBody RecordRequest request) {
        Record record = new Record();

        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentId(request.enrollmentId());
        record.setEnrollment(enrollment);

        Employee employee = new Employee();
        employee.setEmployeeId(request.employeeId());
        record.setEmployee(employee);

        Offense offense = new Offense();
        offense.setOffenseId(request.offenseId());
        record.setOffense(offense);

        record.setDateOfViolation(request.dateOfViolation());

        Record saved = recordService.create(record);
        return ResponseEntity.ok(toResponse(saved));
    }

    /** Prefect resolves a pending record with the sanction imposed. */
    @PutMapping("/{recordId}/resolve")
    @PreAuthorize("hasAuthority('ROLE_PREFECT')")
    public RecordResponse resolve(@PathVariable Long recordId, @RequestBody RecordResolveRequest request) {
        Record resolved = recordService.resolve(recordId, request.actionId(), request.remarks());
        return toResponse(resolved);
    }

    private static RecordResponse toResponse(Record record) {
        Enrollment enrollment = record.getEnrollment();
        Employee employee = record.getEmployee();
        Offense offense = record.getOffense();
        DisciplinaryAction action = record.getAction();

        return new RecordResponse(
                record.getRecordId(),
                enrollment != null ? enrollment.getEnrollmentId() : null,
                enrollment != null && enrollment.getStudent() != null ? enrollment.getStudent().getStudentId() : null,
                enrollment != null && enrollment.getStudent() != null && enrollment.getStudent().getPerson() != null
                        ? enrollment.getStudent().getPerson().getFirstName() + " " + enrollment.getStudent().getPerson().getLastName()
                        : null,
                enrollment != null ? enrollment.getSchoolYear() : null,
                employee != null ? employee.getEmployeeId() : null,
                employee != null && employee.getPerson() != null
                        ? employee.getPerson().getFirstName() + " " + employee.getPerson().getLastName()
                        : null,
                offense != null ? offense.getOffenseId() : null,
                offense != null ? offense.getOffense() : null,
                record.getDateOfViolation(),
                action != null ? action.getActionId() : null,
                action != null ? action.getAction() : null,
                record.getDateOfResolution(),
                record.getRemarks(),
                record.getStatus()
        );
    }
}
