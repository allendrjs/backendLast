package org.rocs.osdrmsa.service.chat.impl;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.appeal.Appeal;
import org.rocs.osdrmsa.domain.enrollment.Enrollment;
import org.rocs.osdrmsa.domain.handbook.HandbookChunk;
import org.rocs.osdrmsa.domain.login.Login;
import org.rocs.osdrmsa.domain.person.student.Student;
import org.rocs.osdrmsa.domain.record.Record;
import org.rocs.osdrmsa.dto.request.ChatRequest;
import org.rocs.osdrmsa.dto.response.ChatResponse;
import org.rocs.osdrmsa.dto.summary.ChatMessageDto;
import org.rocs.osdrmsa.repository.appeal.AppealRepository;
import org.rocs.osdrmsa.repository.enrollment.EnrollmentRepository;
import org.rocs.osdrmsa.repository.handbook.HandbookChunkRepository;
import org.rocs.osdrmsa.repository.login.LoginRepository;
import org.rocs.osdrmsa.repository.record.RecordRepository;
import org.rocs.osdrmsa.repository.student.StudentRepository;
import org.rocs.osdrmsa.service.chat.ChatService;
import org.rocs.osdrmsa.utils.ai.OllamaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatServiceImpl.class);

    private static final int MAX_RECENT_ITEMS = 10;

    private static final String SYSTEM_PROMPT = """
            You are the RC-OSD Assistant, a support chatbot inside the Rogationist College Office for \
            Student Discipline mobile app. You help the currently logged-in student understand their own \
            disciplinary records, appeal history, how to file an appeal, and the policies in their \
            Department's Student Handbook.

            Rules you must follow:
            1. Only use the student information in the CONTEXT block and the excerpts in the HANDBOOK \
            EXCERPTS block below. Never invent offenses, dates, statuses, policies, or rules that are not \
            explicitly present in those blocks.
            2. Do not give legal advice, and do not judge whether the student is guilty or innocent of an offense.
            3. A student can file an appeal on a PENDING offense from that offense's detail screen in the app.
            4. When asked a school-policy question (dress code, attendance, offenses and sanctions, \
            anti-bullying, etc.), answer using only the HANDBOOK EXCERPTS provided. If the excerpts don't \
            cover what was asked, say the handbook doesn't seem to cover that and suggest the student \
            contact the Office for Student Discipline directly. Never answer a policy question from your \
            own general knowledge.
            5. If asked about something with no connection to the student's own records or their handbook \
            (other students, unrelated topics), say you can't help with that.
            6. Keep answers short and easy to read on a phone screen.""";

    private static final int HANDBOOK_TOP_K_DEFAULT = 5;

    private final LoginRepository loginRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final RecordRepository recordRepository;
    private final AppealRepository appealRepository;
    private final HandbookChunkRepository handbookChunkRepository;
    private final OllamaClient ollamaClient;

    @Value("${handbook.top-k:5}")
    private int handbookTopK = HANDBOOK_TOP_K_DEFAULT;

    @Override
    public ChatResponse ask(String username, ChatRequest request) {
        if (request == null || request.message() == null || request.message().isBlank()) {
            throw new IllegalArgumentException("Message is required.");
        }

        String userMessage = request.message().trim();
        Student student = resolveStudent(username);
        String context = buildContext(student, userMessage);

        List<ChatMessageDto> messages = new ArrayList<>();
        messages.add(new ChatMessageDto("system", SYSTEM_PROMPT + "\n\n" + context));

        if (request.history() != null) {
            messages.addAll(request.history());
        }
        messages.add(new ChatMessageDto("user", userMessage));

        String reply = ollamaClient.chat(messages);

        return new ChatResponse(reply, LocalDateTime.now());
    }

    private Student resolveStudent(String username) {
        Login login = loginRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("No account found for the current session."));

        if (login.getPerson() == null) {
            throw new IllegalStateException("This account isn't linked to a student profile.");
        }

        return studentRepository.findByPerson_PersonId(login.getPerson().getPersonId())
                .orElseThrow(() -> new IllegalStateException("No student profile found for the current session."));
    }

    private String buildContext(Student student, String userMessage) {
        StringBuilder sb = new StringBuilder();
        sb.append("CONTEXT:\n");
        sb.append("Student ID: ").append(student.getStudentId()).append("\n");

        Enrollment enrollment = enrollmentRepository
                .findTopByStudentStudentIdOrderBySchoolYearDesc(student.getStudentId())
                .orElse(null);

        if (enrollment != null) {
            sb.append("Current Enrollment: ")
                    .append(enrollment.getSchoolYear()).append(", ")
                    .append(enrollment.getStudentLevel()).append(", Section ")
                    .append(enrollment.getSection()).append("\n");
            if (enrollment.getDisciplinaryStatus() != null) {
                sb.append("Disciplinary Status: ")
                        .append(enrollment.getDisciplinaryStatus().getStatus()).append("\n");
            }
        } else {
            sb.append("Current Enrollment: none on file\n");
        }

        List<Record> records = recordRepository.findByEnrollmentStudentStudentId(student.getStudentId());
        appendRecords(sb, records);

        List<Appeal> appeals = appealRepository.findByEnrollmentStudentStudentId(student.getStudentId());
        appendAppeals(sb, appeals);

        String department = enrollment != null && enrollment.getDepartment() != null
                ? enrollment.getDepartment().name()
                : null;
        sb.append("\n").append(buildHandbookExcerpts(department, userMessage));

        return sb.toString();
    }

    private String buildHandbookExcerpts(String department, String userMessage) {
        if (department == null) {
            return "HANDBOOK EXCERPTS: unavailable (no enrollment/department on file for this student).";
        }

        try {
            float[] queryEmbedding = ollamaClient.embed(userMessage);
            List<HandbookChunk> matches = handbookChunkRepository.findNearest(department, queryEmbedding, handbookTopK);

            if (matches.isEmpty()) {
                return "HANDBOOK EXCERPTS: none available for department " + department + ".";
            }

            StringBuilder sb = new StringBuilder("HANDBOOK EXCERPTS (from the ")
                    .append(department).append(" Student Handbook, most relevant to the student's latest message):\n");
            for (HandbookChunk chunk : matches) {
                sb.append("- [").append(chunk.sectionTitle()).append("] ")
                        .append(chunk.content()).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            log.warn("Handbook retrieval failed for department {}: {}", department, e.getMessage());
            return "HANDBOOK EXCERPTS: temporarily unavailable.";
        }
    }

    private void appendRecords(StringBuilder sb, List<Record> records) {
        sb.append("\nDisciplinary Records (").append(records.size()).append(" total");
        if (records.size() > MAX_RECENT_ITEMS) {
            sb.append(", showing ").append(MAX_RECENT_ITEMS).append(" most recent");
        }
        sb.append("):\n");

        if (records.isEmpty()) {
            sb.append("- No disciplinary records on file.\n");
            return;
        }

        records.stream()
                .sorted(Comparator.comparing(Record::getDateOfViolation, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(MAX_RECENT_ITEMS)
                .forEach(r -> sb.append("- Record #").append(r.getRecordId())
                        .append(": ").append(r.getOffense() != null ? r.getOffense().getOffense() : "Unknown offense")
                        .append(", filed ").append(r.getDateOfViolation())
                        .append(", status ").append(r.getStatus())
                        .append("\n"));
    }

    private void appendAppeals(StringBuilder sb, List<Appeal> appeals) {
        sb.append("\nAppeals (").append(appeals.size()).append(" total");
        if (appeals.size() > MAX_RECENT_ITEMS) {
            sb.append(", showing ").append(MAX_RECENT_ITEMS).append(" most recent");
        }
        sb.append("):\n");

        if (appeals.isEmpty()) {
            sb.append("- No appeals on file.\n");
            return;
        }

        appeals.stream()
                .sorted(Comparator.comparing(Appeal::getDateFiled, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(MAX_RECENT_ITEMS)
                .forEach(a -> sb.append("- Appeal #").append(a.getAppealId())
                        .append(" for ").append(a.getRecord() != null && a.getRecord().getOffense() != null
                                ? a.getRecord().getOffense().getOffense() : "an offense")
                        .append(", filed ").append(a.getDateFiled())
                        .append(", status ").append(a.getStatus())
                        .append("\n"));
    }
}
