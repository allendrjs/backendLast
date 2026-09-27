package org.rocs.osdrmsa.controller.student;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.department.Department;
import org.rocs.osdrmsa.domain.person.student.Student;
import org.rocs.osdrmsa.service.student.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Student>> getAll(
            @RequestParam(required = false) Department department) {

        if (department != null) {
            return ResponseEntity.ok(studentService.getByDepartment(department));
        }

        return ResponseEntity.ok(studentService.getAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Student>> getActiveStudents() {
        return ResponseEntity.ok(studentService.getActive());
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<List<Student>> getStudentsByDepartment(@PathVariable Department department) {
        return ResponseEntity.ok(studentService.getByDepartment(department));
    }

    @GetMapping("/department/{department}/active")
    public ResponseEntity<List<Student>> getActiveStudentsByDepartment(@PathVariable Department department) {
        return ResponseEntity.ok(studentService.getByDepartmentActive(department));
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<Student> getStudent(@PathVariable String studentId) {
        return studentService.getById(studentId).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{studentId}/active")
    public ResponseEntity<Student> getActiveStudent(@PathVariable String studentId) {
        return studentService.getActiveById(studentId).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/person/{personId}")
    public ResponseEntity<Student> getStudentByPersonId(@PathVariable Long personId) {
        return studentService.getByPersonId(personId).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        Student created = studentService.create(student);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{studentId}")
    public ResponseEntity<Student> updateStudent(@PathVariable String studentId, @RequestBody Student student) {
        Student updated = studentService.update(studentId, student);

        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{studentId}/status")
    public ResponseEntity<Student> updateStudentStatus(@PathVariable String studentId, @RequestBody Map<String, Boolean> request) {
        Boolean active = request.get("isActive");

        if (active == null) {
            return ResponseEntity.badRequest().build();
        }

        Student student = studentService.setActive(studentId, active);

        return ResponseEntity.ok(student);
    }

    @DeleteMapping("/{studentId}")
    public ResponseEntity<Void> deleteStudent(@PathVariable String studentId) {
        if (studentService.getById(studentId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        studentService.delete(studentId);

        return ResponseEntity.noContent().build();
    }
}