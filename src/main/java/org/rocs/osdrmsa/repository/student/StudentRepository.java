package org.rocs.osdrmsa.repository.student;

import org.rocs.osdrmsa.domain.department.Department;
import org.rocs.osdrmsa.domain.person.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {

    List<Student> findByDepartment(Department department);
}
