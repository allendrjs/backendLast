package org.rocs.osdrmsa.utils.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rocs.osdrmsa.domain.login.Login;
import org.rocs.osdrmsa.domain.person.Person;
import org.rocs.osdrmsa.domain.person.student.Student;
import org.rocs.osdrmsa.repository.login.LoginRepository;
import org.rocs.osdrmsa.repository.student.StudentRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OwnAccessEvaluatorTest {

    @Mock
    private LoginRepository loginRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private OwnAccessEvaluator ownAccessEvaluator;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateAs(String username) {
        var auth = new UsernamePasswordAuthenticationToken(username, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private Person personWithId(long id) {
        Person person = new Person();
        person.setPersonID(id);
        return person;
    }

    @Test
    void isSelfStudent_falseWhenUnauthenticated() {
        assertThat(ownAccessEvaluator.isSelfStudent("STU-001")).isFalse();
    }

    @Test
    void isSelfStudent_falseWhenBlankStudentId() {
        authenticateAs("jdoe");
        assertThat(ownAccessEvaluator.isSelfStudent(" ")).isFalse();
    }

    @Test
    void isSelfStudent_falseWhenLoginHasNoLinkedPerson() {
        authenticateAs("jdoe");
        Login login = new Login();
        login.setUsername("jdoe");
        when(loginRepository.findByUsername("jdoe")).thenReturn(Optional.of(login));

        assertThat(ownAccessEvaluator.isSelfStudent("STU-001")).isFalse();
    }

    @Test
    void isSelfStudent_falseWhenStudentBelongsToDifferentPerson() {
        authenticateAs("jdoe");

        Login login = new Login();
        login.setUsername("jdoe");
        login.setPerson(personWithId(1L));
        when(loginRepository.findByUsername("jdoe")).thenReturn(Optional.of(login));

        Student student = new Student();
        student.setStudentId("STU-001");
        student.setPerson(personWithId(2L));
        when(studentRepository.findById("STU-001")).thenReturn(Optional.of(student));

        assertThat(ownAccessEvaluator.isSelfStudent("STU-001")).isFalse();
    }

    @Test
    void isSelfStudent_trueWhenStudentBelongsToSamePerson() {
        authenticateAs("jdoe");

        Login login = new Login();
        login.setUsername("jdoe");
        login.setPerson(personWithId(42L));
        when(loginRepository.findByUsername("jdoe")).thenReturn(Optional.of(login));

        Student student = new Student();
        student.setStudentId("STU-001");
        student.setPerson(personWithId(42L));
        when(studentRepository.findById("STU-001")).thenReturn(Optional.of(student));

        assertThat(ownAccessEvaluator.isSelfStudent("STU-001")).isTrue();
    }
}
