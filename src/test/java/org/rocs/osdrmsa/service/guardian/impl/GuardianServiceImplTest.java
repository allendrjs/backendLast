package org.rocs.osdrmsa.service.guardian.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rocs.osdrmsa.domain.person.guardian.Guardian;
import org.rocs.osdrmsa.domain.person.student.Student;
import org.rocs.osdrmsa.repository.student.StudentRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GuardianServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private GuardianServiceImpl guardianService;

    @Test
    void getByStudentId_returnsStudentsGuardians() {
        Guardian guardian = new Guardian();
        guardian.setGuardianID(1L);
        guardian.setRelationship("Mother");

        Student student = new Student();
        student.setStudentId("JHS-0001");
        student.setGuardians(List.of(guardian));

        when(studentRepository.findById("JHS-0001")).thenReturn(Optional.of(student));

        List<Guardian> result = guardianService.getByStudentId("JHS-0001");

        assertThat(result).containsExactly(guardian);
    }

    @Test
    void getByStudentId_throwsWhenStudentNotFound() {
        when(studentRepository.findById("GHOST")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> guardianService.getByStudentId("GHOST"))
                .isInstanceOf(java.util.NoSuchElementException.class);
    }
}
