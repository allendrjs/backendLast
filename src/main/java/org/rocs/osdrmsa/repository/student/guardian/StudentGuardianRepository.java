package org.rocs.osdrmsa.repository.student.guardian;

import org.rocs.osdrmsa.domain.person.student.guardian.StudentGuardian;
import org.rocs.osdrmsa.domain.person.student.guardian.StudentGuardianId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This was previously an empty marker interface (didn't even extend
 * JpaRepository) - filled in so GuardianService can actually look up which
 * guardians belong to a student through the StudentGuardian join entity.
 */
@Repository
public interface StudentGuardianRepository extends JpaRepository<StudentGuardian, StudentGuardianId> {

    List<StudentGuardian> findByStudent_StudentId(String studentId);
}
