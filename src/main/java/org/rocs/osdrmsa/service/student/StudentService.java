package org.rocs.osdrmsa.service.student;

import org.rocs.osdrmsa.domain.department.Department;
import org.rocs.osdrmsa.domain.person.student.Student;

import java.util.List;
import java.util.Optional;

public interface StudentService {

    List<Student> getAll();

    List<Student> getActive();

    List<Student> getByDepartment(Department department);

    List<Student> getByDepartmentActive(Department department);

    Optional<Student> getById(String studentId);

    Optional<Student> getActiveById(String studentId);

    Optional<Student> getByPersonId(Long personId);

    Student create(Student student);

    Student update(String studentId, Student student);

    Student setActive(String studentId, boolean active);

    void delete(String studentId);
}
