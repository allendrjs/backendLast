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

    Optional<Student> findByPerson_PersonId(Long personId);

    List<Student> findByIsActiveTrue();

    List<Student> findByDepartmentAndIsActiveTrue(Department department);

    Optional<Student> findByStudentIdAndIsActiveTrue(String studentId);

}
