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
    private final AiCaseAnalysisService aiCaseAnalysisService;
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

        AiCaseAnalysisService.Result aiRecommendation =
                generateAiRecommendation(request, matchingRecords, department);
        request.setAiRecommendation(aiRecommendation.recommendation());
        request.setAiReasoning(aiRecommendation.reasoning());

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

        throw new IllegalArgumentException(
                "Unsupported request type: " + type
                        + ". Department head requests may only be filed by student."
        );
    }

    private String buildRequestContext(Request request, List<Record> records) {

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

        return context.toString();
    }

    private String generateAiResponse(
            Request request,
            List<Record> records) {

        try {
            return ollamaClient.chat(
                    List.of(
                            new ChatMessageDto(
                                    "system",
                                    AI_SYSTEM_PROMPT
                            ),
                            new ChatMessageDto(
                                    "user",
                                    buildRequestContext(request, records)
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

    private AiCaseAnalysisService.Result generateAiRecommendation(
            Request request,
            List<Record> records,
            Department department) {

        try {
            String context = buildRequestContext(request, records);
            String departmentName = department != null ? department.name() : null;
            return aiCaseAnalysisService.analyze("Department Head Request", departmentName, context);
        } catch (Exception e) {
            log.warn(
                    "AI Support Module request recommendation generation failed: {}",
                    e.getMessage()
            );
            return new AiCaseAnalysisService.Result("UNCERTAIN", "AI analysis is temporarily unavailable.");
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

        Request saved = requestRepository.save(request);

        return saved;
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

            List<Enrollment> enrollments =
                    enrollmentRepository.findByStudentStudentIdAndDepartment(
                            request.getDetails().trim(),
                            department
                    );

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
