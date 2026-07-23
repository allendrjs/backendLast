package org.rocs.osdrmsa.controller.student;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.department.Department;
import org.rocs.osdrmsa.domain.person.student.Student;
import org.rocs.osdrmsa.service.student.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'PREFECT')")
    public ResponseEntity<List<Student>> getAll(
            @RequestParam(required = false) Department department) {
        if (department != null) {
            return ResponseEntity.ok(studentService.getByDepartment(department));
        }
        return ResponseEntity.ok(studentService.getAll());
    }

    @GetMapping("/{studentId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'PREFECT', 'DEPARTMENT_HEAD') "
            + "or (hasRole('STUDENT') and @access.isSelfStudent(#studentId))")
    public ResponseEntity<Student> getById(@PathVariable String studentId) {
        return studentService.getById(studentId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Student> create(@RequestBody Student student) {
        return ResponseEntity.ok(studentService.create(student));
    }

    @PutMapping("/{studentId}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Student> update(
            @PathVariable String studentId, @RequestBody Student student) {
        return ResponseEntity.ok(studentService.update(studentId, student));
    }

    @DeleteMapping("/{studentId}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Void> delete(@PathVariable String studentId) {
        studentService.delete(studentId);
        return ResponseEntity.noContent().build();
    }
}
