package org.rocs.osdrmsa.service.student;

import org.rocs.osdrmsa.domain.department.Department;
import org.rocs.osdrmsa.domain.person.student.Student;

import java.util.List;
import java.util.Optional;

public interface StudentService {

    List<Student> getAll();

    List<Student> getByDepartment(Department department);

    Optional<Student> getById(String studentId);

    Student create(Student student);

    Student update(String studentId, Student student);

    void delete(String studentId);
}
