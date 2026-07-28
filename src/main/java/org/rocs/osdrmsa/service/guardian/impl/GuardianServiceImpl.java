package org.rocs.osdrmsa.service.guardian.impl;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.person.guardian.Guardian;
import org.rocs.osdrmsa.repository.student.guardian.StudentGuardianRepository;
import org.rocs.osdrmsa.service.guardian.GuardianService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Student has no "guardians" collection field of its own - the STUDENT and
 * GUARDIAN tables are only related through the STUDENTGUARDIAN join table,
 * so guardians for a student are read through StudentGuardianRepository and
 * unwrapped here, rather than through the Student entity directly.
 */
@Service
@RequiredArgsConstructor
public class GuardianServiceImpl implements GuardianService {

    private final StudentGuardianRepository studentGuardianRepository;

    @Override
    public List<Guardian> getByStudentId(String studentId) {
        return studentGuardianRepository.findByStudent_StudentId(studentId).stream()
                .map(sg -> sg.getGuardian())
                .toList();
    }
}
