package org.rocs.osdrmsa.service.guardian.impl;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.person.guardian.Guardian;
import org.rocs.osdrmsa.domain.person.student.Student;
import org.rocs.osdrmsa.repository.student.StudentRepository;
import org.rocs.osdrmsa.service.guardian.GuardianService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Guardians are read through Student's studentGuardian mapping rather than
 * GuardianRepository directly, since "which guardians belong to which
 * student" is the only lookup this API currently needs (matching the
 * desktop app's existing GuardianDao.findGuardianByStudentId). Creating,
 * updating, or unlinking guardians is out of scope here - no client needs
 * it yet.
 */
@Service
@RequiredArgsConstructor
public class GuardianServiceImpl implements GuardianService {

    private final StudentRepository studentRepository;

    @Override
    public List<Guardian> getByStudentId(String studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NoSuchElementException("Student not found: " + studentId));
        return student.getGuardians();
    }
}
