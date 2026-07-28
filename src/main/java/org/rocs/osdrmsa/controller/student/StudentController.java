package org.rocs.osdrmsa.controller.student;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.department.Department;
import org.rocs.osdrmsa.domain.login.Login;
import org.rocs.osdrmsa.domain.person.Person;
import org.rocs.osdrmsa.domain.person.student.Student;
import org.rocs.osdrmsa.dto.request.StudentRequest;
import org.rocs.osdrmsa.dto.response.StudentResponse;
import org.rocs.osdrmsa.service.login.LoginService;
import org.rocs.osdrmsa.service.student.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Student record lookup and management.
 *
 * Read/list endpoints are open to Admin, Prefect, and Staff (Department
 * Head) - Dept Head's whole job is searching and requesting records, so
 * they need to be able to look students up. Create/update/delete are
 * Admin-only: adding or changing student master data is an administrative
 * function, not something Prefect or Dept Head do through this API.
 * /me is student-facing - resolves the logged-in student's own record from
 * their JWT identity rather than trusting a client-supplied studentId.
 */
@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final LoginService loginService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PREFECT','ROLE_STAFF')")
    public List<StudentResponse> getAll(@RequestParam(required = false) Department department) {
        List<Student> students = department != null
                ? studentService.getByDepartment(department)
                : studentService.getAll();

        return students.stream().map(StudentController::toResponse).toList();
    }

    @GetMapping("/{studentId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PREFECT','ROLE_STAFF')")
    public StudentResponse getById(@PathVariable String studentId) {
        Student student = studentService.getById(studentId)
                .orElseThrow(() -> new NoSuchElementException("Student not found: " + studentId));
        return toResponse(student);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public StudentResponse getOwnRecord(Authentication authentication) {
        Login login = loginService.getByUsername(authentication.getName())
                .orElseThrow(() -> new NoSuchElementException("Login not found."));

        if (login.getPerson() == null) {
            throw new NoSuchElementException("This account has no linked person record.");
        }

        Student student = studentService.getByPersonId(login.getPerson().getPersonId())
                .orElseThrow(() -> new NoSuchElementException("No student record linked to this account."));

        return toResponse(student);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<StudentResponse> create(@RequestBody StudentRequest request) {
        Student student = toEntity(request);
        student.setStudentId(request.studentId());
        Student saved = studentService.create(student);
        return ResponseEntity.ok(toResponse(saved));
    }

    @PutMapping("/{studentId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public StudentResponse update(@PathVariable String studentId, @RequestBody StudentRequest request) {
        Student updated = studentService.update(studentId, toEntity(request));
        return toResponse(updated);
    }

    @DeleteMapping("/{studentId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String studentId) {
        studentService.delete(studentId);
        return ResponseEntity.noContent().build();
    }

    private static Student toEntity(StudentRequest request) {
        Student student = new Student();

        if (request.personId() != null) {
            Person person = new Person();
            person.setPersonId(request.personId());
            student.setPerson(person);
        }

        student.setAddress(request.address());
        student.setStudentType(request.studentType());
        student.setDepartment(request.department());
        return student;
    }

    private static StudentResponse toResponse(Student student) {
        Person person = student.getPerson();
        return new StudentResponse(
                student.getStudentId(),
                person != null ? person.getPersonId() : null,
                person != null ? person.getFirstName() : null,
                person != null ? person.getLastName() : null,
                person != null ? person.getMiddleName() : null,
                student.getAddress(),
                student.getStudentType(),
                student.getDepartment()
        );
    }
}
