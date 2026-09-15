package org.rocs.osdrmsa.service.request.impl;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.dto.summary.ChatMessageDto;
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
    private final OllamaClient ollamaClient;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;

    private static final Logger log =
            LoggerFactory.getLogger(RequestServiceImpl.class);

    private static final String AI_SYSTEM_PROMPT = """
            You are the AI Support Module inside the Rogationist College Office for Student Discipline system.
            Your job is to provide a short, neutral, informational response to a Department Head's request for disciplinary records.

            Rules:
            1. Use only the REQUEST CONTEXT provided. Never invent students, records, offenses, dates, or other facts.
            2. Do not approve or deny the request.
            3. Do not make disciplinary decisions.
            4. Explain what records are on file and what the request is asking for.
            5. If the requested scope contains no matching records, say so clearly.
            6. Keep the response to 2-4 sentences, plain language, and no headers or bullet points.
            7. State that the response is informational and does not constitute approval or denial.
            """;

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

        Department department = employee.getDepartment();

        if (department == null) {
            throw new IllegalStateException(
                    "No department is assigned to your employee account."
            );
        }

        List<Record> matchingRecords =
                findMatchingRecords(request, department);

        request.setEmployeeID(employee.getEmployeeId());
        request.setRequestID(0);
        request.setStatus(RequestStatus.PENDING);
        request.setDateFiled(LocalDate.now());
        request.setDateProcessed(null);
        request.setRemarks(null);

        request.setAiResponse(
                generateAiResponse(request, matchingRecords)
        );

        return requestRepository.save(request);
    }

    private List<Record> findMatchingRecords(
            Request request,
            Department department) {

        String type = request.getType().trim();
        String details = request.getDetails().trim();

        if (type.equalsIgnoreCase("By Student")) {

            List<Enrollment> enrollments =
                    enrollmentRepository.findByStudentStudentIdAndDepartment(
                            details,
                            department
                    );

            if (enrollments.isEmpty()) {
                throw new IllegalArgumentException(
                        "Student ID '" + details +
                                "' was not found among the enrolled students " +
                                "in your department."
                );
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

    private String generateAiResponse(
            Request request,
            List<Record> records) {

        try {

            StringBuilder context = new StringBuilder();

            context.append("REQUEST TYPE: ")
                    .append(request.getType())
                    .append("\n");

            context.append("REQUEST DETAILS: ")
                    .append(request.getDetails())
                    .append("\n");

            context.append("REQUEST REASON: ")
                    .append(request.getMessage())
                    .append("\n\n");

            context.append(
                            "MATCHING DISCIPLINARY RECORDS ON FILE ("
                    ).append(records.size())
                    .append("):\n");

            if (records.isEmpty()) {
                context.append(
                        "No disciplinary records are currently on file "
                                + "for this valid requested scope.\n"
                );
            } else {
                records.stream()
                        .limit(20)
                        .forEach(record -> {

                            String studentId =
                                    record.getEnrollment() != null
                                            && record.getEnrollment().getStudent() != null
                                            ? record.getEnrollment()
                                            .getStudent()
                                            .getStudentId()
                                            : "Unknown";

                            String offense =
                                    record.getOffense() != null
                                            ? record.getOffense().getOffense()
                                            : "Unknown";

                            context.append("- Student: ")
                                    .append(studentId)
                                    .append(", offense: ")
                                    .append(offense)
                                    .append(", violation date: ")
                                    .append(record.getDateOfViolation())
                                    .append(", status: ")
                                    .append(record.getStatus())
                                    .append("\n");
                        });
            }

            return ollamaClient.chat(
                    List.of(
                            new ChatMessageDto(
                                    "system",
                                    AI_SYSTEM_PROMPT
                            ),
                            new ChatMessageDto(
                                    "user",
                                    context.toString()
                            )
                    )
            );

        } catch (Exception e) {

            log.warn(
                    "AI Support Module request response generation failed: {}",
                    e.getMessage()
            );

            return null;
        }
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
}
