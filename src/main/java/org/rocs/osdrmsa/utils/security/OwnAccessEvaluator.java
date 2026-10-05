package org.rocs.osdrmsa.utils.security;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.appeal.Appeal;
import org.rocs.osdrmsa.domain.login.Login;
import org.rocs.osdrmsa.domain.person.student.Student;
import org.rocs.osdrmsa.domain.document.Document;
import org.rocs.osdrmsa.domain.enrollment.Enrollment;
import org.rocs.osdrmsa.domain.record.Record;
import org.rocs.osdrmsa.repository.appeal.AppealRepository;
import org.rocs.osdrmsa.repository.document.DocumentRepository;
import org.rocs.osdrmsa.repository.employee.EmployeeRepository;
import org.rocs.osdrmsa.repository.enrollment.EnrollmentRepository;
import org.rocs.osdrmsa.repository.record.RecordRepository;
import org.rocs.osdrmsa.repository.login.LoginRepository;
import org.rocs.osdrmsa.repository.student.StudentRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("access")
@RequiredArgsConstructor
public class OwnAccessEvaluator {

    private final LoginRepository loginRepository;
    private final StudentRepository studentRepository;
    private final AppealRepository appealRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final RecordRepository recordRepository;
    private final DocumentRepository documentRepository;
    private final EmployeeRepository employeeRepository;

    public boolean isSelfAppeal(Long appealId) {

        if (appealId == null) {
            return false;
        }

        String username = currentUsername();
        if (username == null) {
            return false;
        }

        Login login = loginRepository.findByUsername(username).orElse(null);
        if (login == null || login.getPerson() == null) {
            return false;
        }

        Appeal appeal = appealRepository.findById(appealId).orElse(null);
        if (appeal == null || appeal.getEnrollment() == null || appeal.getEnrollment().getStudent() == null) {
            return false;
        }

        Student student = appeal.getEnrollment().getStudent();
        return student.getPerson() != null
                && student.getPerson().getPersonId().equals(login.getPerson().getPersonId());
    }

    public boolean isSelfStudent(String studentId) {

        if (studentId == null || studentId.isBlank()) {
            return false;
        }

        String username = currentUsername();
        if (username == null) {
            return false;
        }

        Login login = loginRepository.findByUsername(username).orElse(null);
        if (login == null || login.getPerson() == null) {
            return false;
        }

        Student student = studentRepository.findById(studentId).orElse(null);
        if (student == null || student.getPerson() == null) {
            return false;
        }

        return student.getPerson().getPersonId()
                .equals(login.getPerson().getPersonId());
    }

    public boolean canFileAppeal(Long recordId, Long enrollmentId, Long documentId) {

        if (recordId == null || enrollmentId == null) {
            return false;
        }

        Long personId = currentPersonId();
        if (personId == null) {
            return false;
        }

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElse(null);
        if (enrollment == null || !belongsToPerson(enrollment.getStudent(), personId)) {
            return false;
        }

        Record record = recordRepository.findById(recordId).orElse(null);
        if (record == null || record.getEnrollment() == null
                || !enrollmentId.equals(record.getEnrollment().getEnrollmentId())) {
            return false;
        }

        if (documentId != null) {
            Document document = documentRepository.findById(documentId).orElse(null);
            if (document == null || !belongsToPerson(document.getStudent(), personId)) {
                return false;
            }
        }

        return true;
    }

    public boolean isSelfEmployee(String employeeId) {

        if (employeeId == null || employeeId.isBlank()) {
            return false;
        }

        Long personId = currentPersonId();
        if (personId == null) {
            return false;
        }

        return employeeRepository.findByPersonPersonId(personId)
                .map(employee -> employeeId.equals(employee.getEmployeeId()))
                .orElse(false);
    }

    private boolean belongsToPerson(Student student, Long personId) {
        return student != null
                && student.getPerson() != null
                && personId.equals(student.getPerson().getPersonId());
    }

    private Long currentPersonId() {
        String username = currentUsername();
        if (username == null) {
            return null;
        }
        Login login = loginRepository.findByUsername(username).orElse(null);
        if (login == null || login.getPerson() == null) {
            return null;
        }
        return login.getPerson().getPersonId();
    }

    private String currentUsername() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }
}