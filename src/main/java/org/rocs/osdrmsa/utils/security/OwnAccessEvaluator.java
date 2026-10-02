package org.rocs.osdrmsa.utils.security;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.appeal.Appeal;
import org.rocs.osdrmsa.domain.login.Login;
import org.rocs.osdrmsa.domain.person.student.Student;
import org.rocs.osdrmsa.repository.appeal.AppealRepository;
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

    /** True only when the logged-in user is the student who filed this appeal. */
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

    private String currentUsername() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }
}