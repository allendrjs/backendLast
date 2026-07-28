package org.rocs.osdrmsa.controller.employee;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.department.Department;
import org.rocs.osdrmsa.domain.person.Person;
import org.rocs.osdrmsa.domain.person.employee.Employee;
import org.rocs.osdrmsa.dto.request.EmployeeRequest;
import org.rocs.osdrmsa.dto.response.EmployeeResponse;
import org.rocs.osdrmsa.service.employee.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Employee (staff) directory - Admin, Prefect, and Dept Head accounts
 * themselves. This is Admin-only end to end, unlike Student/Offense -
 * staff directory management is a System Administration function, not
 * something Prefect or Dept Head need to browse through this API.
 */
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<EmployeeResponse> getAll(@RequestParam(required = false) Department department) {
        List<Employee> employees = department != null
                ? employeeService.getByDepartment(department)
                : employeeService.getAll();
        return employees.stream().map(EmployeeController::toResponse).toList();
    }

    @GetMapping("/{employeeId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EmployeeResponse getById(@PathVariable String employeeId) {
        return employeeService.getById(employeeId).map(EmployeeController::toResponse)
                .orElseThrow(() -> new NoSuchElementException("Employee not found: " + employeeId));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<EmployeeResponse> create(@RequestBody EmployeeRequest request) {
        Employee employee = toEntity(request);
        employee.setEmployeeId(request.employeeId());
        return ResponseEntity.ok(toResponse(employeeService.create(employee)));
    }

    @PutMapping("/{employeeId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EmployeeResponse update(@PathVariable String employeeId, @RequestBody EmployeeRequest request) {
        return toResponse(employeeService.update(employeeId, toEntity(request)));
    }

    @DeleteMapping("/{employeeId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String employeeId) {
        employeeService.delete(employeeId);
        return ResponseEntity.noContent().build();
    }

    private static Employee toEntity(EmployeeRequest request) {
        Employee employee = new Employee();
        if (request.personId() != null) {
            Person person = new Person();
            person.setPersonId(request.personId());
            employee.setPerson(person);
        }
        employee.setDepartment(request.department());
        employee.setEmployeeRole(request.employeeRole());
        return employee;
    }

    private static EmployeeResponse toResponse(Employee employee) {
        Person person = employee.getPerson();
        return new EmployeeResponse(
                employee.getEmployeeId(),
                person != null ? person.getPersonId() : null,
                person != null ? person.getFirstName() : null,
                person != null ? person.getLastName() : null,
                employee.getDepartment(),
                employee.getEmployeeRole()
        );
    }
}
