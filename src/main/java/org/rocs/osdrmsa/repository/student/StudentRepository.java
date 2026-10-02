package org.rocs.osdrmsa.repository.student;

import org.rocs.osdrmsa.domain.department.Department;
import org.rocs.osdrmsa.domain.person.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * Matches on student ID, first name, or last name (case-insensitive,
     * substring). Used by the prefect-facing "find a student" search when
     * recording a new offense, since prefects typically know a name or a
     * partial ID, not an exact one.
     */
    @Query("SELECT s FROM Student s WHERE "
            + "LOWER(s.studentId) LIKE LOWER(CONCAT('%', :query, '%')) "
            + "OR LOWER(s.person.firstName) LIKE LOWER(CONCAT('%', :query, '%')) "
            + "OR LOWER(s.person.lastName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Student> search(@Param("query") String query);

}

