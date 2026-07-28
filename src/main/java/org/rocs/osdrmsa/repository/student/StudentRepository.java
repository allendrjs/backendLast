package org.rocs.osdrmsa.repository.student;

import org.rocs.osdrmsa.domain.department.Department;
import org.rocs.osdrmsa.domain.person.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {

    Optional<Student> findByStudentId(String studentId);

    List<Student> findByDepartment(Department department);

    /**
     * Resolves a Student by the Person it's linked to (Student.person is a
     * OneToOne on PERSONID). Used to look up the logged-in user's own
     * studentId from the JWT's personId claim, so a client knows which
     * studentId to call the student-scoped endpoints with right after login.
     */
    Optional<Student> findByPerson_PersonId(Long personId);
}
