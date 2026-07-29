package org.rocs.osdrmsa.utils.security;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.login.Login;
import org.rocs.osdrmsa.domain.person.student.Student;
import org.rocs.osdrmsa.repository.login.LoginRepository;
import org.rocs.osdrmsa.repository.student.StudentRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * SpEL-callable checks used from @PreAuthorize so
 * a STUDENT-role token can only reach data belonging to
 * the account it authenticated as
 */
@Component("access")
@RequiredArgsConstructor
public class OwnAccessEvaluator {

    private final LoginRepository loginRepository;
    private final StudentRepository studentRepository;

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

        return student.getPerson().getPersonId() == login.getPerson().getPersonId();
    }

    private String currentUsername() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }
}