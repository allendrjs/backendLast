package org.rocs.osdrmsa.controller.employee;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.department.Department;
import org.rocs.osdrmsa.domain.person.employee.Employee;
import org.rocs.osdrmsa.service.employee.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRATOR')")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<List<Employee>> getAll(
            @RequestParam(required = false) Department department) {
        if (department != null) {
            return ResponseEntity.ok(employeeService.getByDepartment(department));
        }
        return ResponseEntity.ok(employeeService.getAll());
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<Employee> getById(@PathVariable String employeeId) {
        return employeeService.getById(employeeId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Employee> create(@RequestBody Employee employee) {
        return ResponseEntity.ok(employeeService.create(employee));
    }

    @PutMapping("/{employeeId}")
    public ResponseEntity<Employee> update(
            @PathVariable String employeeId, @RequestBody Employee employee) {
        return ResponseEntity.ok(employeeService.update(employeeId, employee));
    }

    @DeleteMapping("/{employeeId}")
    public ResponseEntity<Void> delete(@PathVariable String employeeId) {
        employeeService.delete(employeeId);
        return ResponseEntity.noContent().build();
    }
}
