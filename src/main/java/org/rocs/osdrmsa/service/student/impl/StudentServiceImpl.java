package org.rocs.osdrmsa.service.student.impl;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.department.Department;
import org.rocs.osdrmsa.domain.login.Role;
import org.rocs.osdrmsa.domain.person.student.Student;
import org.rocs.osdrmsa.repository.login.LoginRepository;
import org.rocs.osdrmsa.repository.student.StudentRepository;
import org.rocs.osdrmsa.service.student.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final LoginRepository loginRepository;

    @Override
    public List<Student> getAll() {
        return studentRepository.findAll();
    }

    @Override
    public List<Student> getActive() {
        return studentRepository.findByIsActiveTrue();
    }

    @Override
    public List<Student> getByDepartment(Department department) {
        return studentRepository.findByDepartment(department);
    }

    @Override
    public List<Student> getByDepartmentActive(Department department) {
        return studentRepository.findByDepartmentAndIsActiveTrue(department);
    }

    @Override
    public Optional<Student> getById(String studentId) {
        return studentRepository.findById(studentId);
    }

    @Override
    public Optional<Student> getActiveById(String studentId) {
        return studentRepository.findByStudentIdAndIsActiveTrue(studentId);
    }

    @Override
    public Optional<Student> getByPersonId(Long personId) {
        return studentRepository.findByPerson_PersonId(personId);
    }

    @Override
    public Student create(Student student) {
        if (student.getStudentId() == null ||
                student.getStudentId().isBlank()) {

            throw new IllegalArgumentException(
                    "studentId is required."
            );
        }

        if (studentRepository.existsById(student.getStudentId())) {
            throw new IllegalArgumentException(
                    "Student " + student.getStudentId() +
                            " already exists."
            );
        }

        return studentRepository.save(student);
    }

    @Override
    public Student update(String studentId, Student student) {

        Student existing = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Student not found: " + studentId
                        )
                );

        existing.setAddress(student.getAddress());
        existing.setStudentType(student.getStudentType());
        existing.setDepartment(student.getDepartment());
        existing.setContactNumber(student.getContactNumber());

        if (student.getPerson() != null &&
                existing.getPerson() != null) {

            existing.getPerson().setDateOfBirth(
                    student.getPerson().getDateOfBirth()
            );
        }

        return studentRepository.save(existing);
    }

    @Override
    @Transactional
    public Student setActive(String studentId, boolean active) {

        Student existing = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Student not found: " + studentId
                        )
                );

        existing.setActive(active);

        if (existing.getPerson() != null &&
                existing.getPerson().getPersonId() != null) {

            Long personId = existing.getPerson().getPersonId();

            loginRepository
                    .findByPerson_PersonIdAndRole(
                            personId,
                            Role.ROLE_USER
                    )
                    .ifPresent(login -> {
                        login.setActive(active);
                        loginRepository.save(login);
                    });
        }

        return studentRepository.save(existing);
    }

    @Override
    public void delete(String studentId) {
        studentRepository.deleteById(studentId);
    }
}
