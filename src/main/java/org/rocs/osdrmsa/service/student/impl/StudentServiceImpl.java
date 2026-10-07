package org.rocs.osdrmsa.service.student.impl;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.department.Department;
import org.rocs.osdrmsa.domain.login.Login;
import org.rocs.osdrmsa.domain.login.Role;
import org.rocs.osdrmsa.domain.person.student.Student;
import org.rocs.osdrmsa.repository.login.LoginRepository;
import org.rocs.osdrmsa.repository.student.StudentRepository;
import org.rocs.osdrmsa.service.student.StudentService;
import org.rocs.osdrmsa.utils.security.DefaultCredentials;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

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
    public List<Student> search(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        return studentRepository.search(query.trim());
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

        Student saved = studentRepository.save(student);

        createLoginIfMissing(saved);

        return saved;
    }

    private void createLoginIfMissing(Student saved) {
        if (saved.getPerson() == null
                || saved.getPerson().getPersonId() == null
                || saved.getPerson().getLastName() == null) {
            return;
        }

        Long personId = saved.getPerson().getPersonId();

        if (loginRepository.findByUsername(saved.getStudentId()).isPresent()
                || loginRepository.findByPerson_PersonId(personId).isPresent()) {
            return;
        }

        Login login = new Login();
        login.setUsername(saved.getStudentId());
        login.setPassword(passwordEncoder.encode(
                DefaultCredentials.passwordFor(saved.getPerson().getLastName())));
        login.setRole(Role.ROLE_USER);
        login.setAuthorities("user:read,user:create,user:update");
        login.setPerson(saved.getPerson());

        loginRepository.save(login);
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
                        if (active) {
                            if (login.isLocked() && existing.getPerson().getLastName() != null) {
                                login.setPassword(passwordEncoder.encode(
                                        DefaultCredentials.passwordFor(existing.getPerson().getLastName())));
                            }
                            login.setLocked(false);
                            login.setFailedLoginAttempts(0);
                        }
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

