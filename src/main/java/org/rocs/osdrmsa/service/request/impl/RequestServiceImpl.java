package org.rocs.osdrmsa.service.request.impl;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.dto.summary.ChatMessageDto;
import org.rocs.osdrmsa.service.ai.AiCaseAnalysisService;
import org.rocs.osdrmsa.utils.ai.OllamaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.rocs.osdrmsa.domain.department.Department;
import org.rocs.osdrmsa.domain.login.Login;
import org.rocs.osdrmsa.domain.person.Person;
import org.rocs.osdrmsa.domain.person.employee.Employee;
import org.rocs.osdrmsa.domain.request.Request;
import org.rocs.osdrmsa.domain.request.RequestStatus;
import org.rocs.osdrmsa.domain.record.Record;
import org.rocs.osdrmsa.domain.record.RecordStatus;
import org.rocs.osdrmsa.domain.enrollment.Enrollment;
import org.rocs.osdrmsa.repository.employee.EmployeeRepository;
import org.rocs.osdrmsa.repository.login.LoginRepository;
import org.rocs.osdrmsa.repository.request.RequestRepository;
import org.rocs.osdrmsa.repository.record.RecordRepository;
import org.rocs.osdrmsa.service.request.RequestService;
import org.springframework.stereotype.Service;
import org.rocs.osdrmsa.domain.person.student.Student;
import org.rocs.osdrmsa.repository.enrollment.EnrollmentRepository;
import org.rocs.osdrmsa.repository.student.StudentRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final LoginRepository loginRepository;
    private final EmployeeRepository employeeRepository;
    private final RecordRepository recordRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;

    private static final Logger log =
            LoggerFactory.getLogger(RequestServiceImpl.class);

    @Override
    public Request submitRequest(Request request, String username) {

        Employee employee = getLoggedInEmployee(username);

        if (request.getType() == null || request.getType().isBlank()) {
            throw new IllegalArgumentException("Request type is required.");
        }

        if (request.getDetails() == null || request.getDetails().isBlank()) {
            throw new IllegalArgumentException("Request details are required.");
        }

        if (request.getMessage() == null || request.getMessage().isBlank()) {
            throw new IllegalArgumentException("Request message is required.");
        }

        String deliveryMethod = request.getDeliveryMethod() == null
                || request.getDeliveryMethod().isBlank()
                ? "HARDCOPY"
                : request.getDeliveryMethod().trim().toUpperCase();

        if (!deliveryMethod.equals("HARDCOPY") && !deliveryMethod.equals("EMAIL")) {
            throw new IllegalArgumentException(
                    "Delivery method must be HARDCOPY or EMAIL."
            );
        }

        request.setDeliveryMethod(deliveryMethod);

        Department department = employee.getDepartment();

        if (department == null) {
            throw new IllegalStateException(
                    "No department is assigned to your employee account."
            );
        }

        findMatchingRecords(request, department);

        request.setEmployeeID(employee.getEmployeeId());
        request.setRequestID(0);
        request.setStatus(RequestStatus.PENDING);
        request.setDateFiled(LocalDateTime.now());
        request.setDateProcessed(null);
        request.setRemarks(null);

        return requestRepository.save(request);
    }

    private List<Record> findMatchingRecords(
            Request request,
            Department department) {

        String type = request.getType().trim();
        String details = request.getDetails().trim();

        if (type.equalsIgnoreCase("By Student")) {

            List<String> studentIds = parseStudentIds(details);

            if (studentIds.isEmpty()) {
                throw new IllegalArgumentException(
                        "Select at least one student."
                );
            }

            List<Enrollment> enrollments = new ArrayList<>();

            for (String studentId : studentIds) {
                List<Enrollment> found =
                        enrollmentRepository.findByStudentStudentIdAndDepartment(
                                studentId,
                                department
                        );

                if (found.isEmpty()) {
                    throw new IllegalArgumentException(
                            "Student ID '" + studentId +
                                    "' was not found among the enrolled students " +
                                    "in your department."
                    );
                }

                enrollments.addAll(found);
            }

            return recordRepository.findByEnrollmentIn(enrollments);
        }

        if (type.equalsIgnoreCase("By Section")) {

            List<Enrollment> enrollments =
                    enrollmentRepository.findByDepartmentAndSectionIgnoreCase(
                            department,
                            details
                    );

            if (enrollments.isEmpty()) {
                throw new IllegalArgumentException(
                        "Section '" + details +
                                "' was not found among the enrolled students " +
                                "in your department."
                );
            }

            return recordRepository.findByEnrollmentIn(enrollments);
        }

        if (type.equalsIgnoreCase("By Batch")) {

            String normalizedLevel = normalizeStudentLevel(details);

            List<Enrollment> enrollments =
                    enrollmentRepository
                            .findByDepartmentAndStudentLevelIgnoreCase(
                                    department,
                                    normalizedLevel
                            );

            if (enrollments.isEmpty()) {
                throw new IllegalArgumentException(
                        "Student level '" + details +
                                "' was not found among the enrolled students " +
                                "in your department."
                );
            }

            return recordRepository.findByEnrollmentIn(enrollments);
        }

        throw new IllegalArgumentException(
                "Unsupported request type: " + type
        );
    }

    private List<String> parseStudentIds(String details) {
        return java.util.Arrays.stream(details.split("[,;\\s]+"))
                .map(String::trim)
                .filter(id -> !id.isEmpty())
                .distinct()
                .toList();
    }

    private String normalizeStudentLevel(String details) {

        String normalized = details
                .trim()
                .replaceAll("\\s+", " ");

        if (normalized.matches("(?i)^grade\\s*-?\\d+$")) {

            String number = normalized
                    .replaceAll("(?i)^grade\\s*-?", "");

            return "Grade-" + number;
        }

        return normalized;
    }

    @Override
    public Request processRequest(
            Long requestId,
            RequestStatus decision,
            String remarks) {

        if (decision != RequestStatus.APPROVED
                && decision != RequestStatus.DENIED) {

            throw new IllegalArgumentException(
                    "A request can only be processed to APPROVED or DENIED."
            );
        }

        Request request =
                requestRepository.findById(requestId)
                        .orElseThrow(() ->
                                new NoSuchElementException(
                                        "Request not found: " + requestId
                                )
                        );

        if (request.getStatus() != RequestStatus.PENDING) {

            throw new IllegalArgumentException(
                    "Request " + requestId
                            + " has already been processed ("
                            + request.getStatus()
                            + ")."
            );
        }

        request.setStatus(decision);
        request.setRemarks(remarks);
        request.setDateProcessed(new Date());

        return requestRepository.save(request);
    }

    @Override
    public List<Request> getByEmployeeId(String employeeId) {
        return requestRepository.findByEmployeeID(employeeId);
    }

    @Override
    public List<Request> getByStatus(RequestStatus status) {
        return requestRepository.findByStatus(status);
    }

    @Override
    public List<Request> getAll() {
        return requestRepository.findAll();
    }

    private Employee getLoggedInEmployee(String username) {

        Login login =
                loginRepository.findByUsername(username)
                        .orElseThrow(() ->
                                new NoSuchElementException(
                                        "Logged-in user not found."
                                )
                        );

        Person person = login.getPerson();

        if (person == null) {
            throw new IllegalStateException(
                    "No person is associated with this account."
            );
        }

        return employeeRepository
                .findByPersonPersonId(person.getPersonId())
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Employee record not found."
                        )
                );
    }

    @Override
    public List<Request> getMyDepartmentRequests(String username) {

        Employee employee =
                getLoggedInEmployee(username);

        Department department =
                employee.getDepartment();

        if (department == null) {
            throw new IllegalStateException(
                    "No department is assigned to this employee."
            );
        }

        List<Employee> employees =
                employeeRepository.findByDepartment(department);

        List<String> employeeIds =
                employees.stream()
                        .map(Employee::getEmployeeId)
                        .toList();

        if (employeeIds.isEmpty()) {
            return List.of();
        }

        return requestRepository.findByEmployeeIDIn(employeeIds);
    }

    @Override
    public String getMyDepartmentName(String username) {

        Employee employee =
                getLoggedInEmployee(username);

        if (employee.getDepartment() == null) {
            throw new IllegalStateException(
                    "No department is assigned to this employee."
            );
        }

        return employee.getDepartment().name();
    }

    @Override
    public String getGraduationEligibility(Request request) {

        if (request == null
                || request.getDetails() == null
                || request.getDetails().isBlank()
                || request.getType() == null
                || !request.getType().trim().equalsIgnoreCase("By Student")) {
            return "UNDER_REVIEW";
        }

        try {
            Employee employee =
                    employeeRepository.findById(request.getEmployeeID())
                            .orElse(null);

            Department department =
                    employee != null ? employee.getDepartment() : null;

            if (department == null) {
                return "UNDER_REVIEW";
            }

            List<Enrollment> enrollments = new ArrayList<>();

            for (String studentId : parseStudentIds(request.getDetails())) {
                enrollments.addAll(
                        enrollmentRepository.findByStudentStudentIdAndDepartment(
                                studentId,
                                department
                        )
                );
            }

            if (enrollments.isEmpty()) {
                return "UNDER_REVIEW";
            }

            List<Record> records =
                    recordRepository.findByEnrollmentIn(enrollments);

            boolean hasOpenCase = records.stream().anyMatch(record ->
                    record.getStatus() == RecordStatus.PENDING
                            || record.getStatus() == RecordStatus.PROCESSING
            );

            return hasOpenCase ? "DISQUALIFIED" : "QUALIFIED";

        } catch (Exception e) {
            log.warn(
                    "Graduation eligibility computation failed for request {}: {}",
                    request.getRequestID(),
                    e.getMessage()
            );
            return "UNDER_REVIEW";
        }
    }
}
