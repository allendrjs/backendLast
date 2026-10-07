package org.rocs.osdrmsa.service.document;

import org.apache.tika.Tika;
import org.rocs.osdrmsa.domain.document.Document;
import org.rocs.osdrmsa.domain.person.student.Student;
import org.rocs.osdrmsa.domain.record.Record;
import org.rocs.osdrmsa.domain.suggestion.GeneratedSuggestion;
import org.rocs.osdrmsa.domain.suggestion.Suggestion;
import org.rocs.osdrmsa.dto.summary.ChatMessageDto;
import org.rocs.osdrmsa.repository.document.DocumentRepository;
import org.rocs.osdrmsa.repository.record.RecordRepository;
import org.rocs.osdrmsa.repository.suggestion.GeneratedSuggestionRepository;
import org.rocs.osdrmsa.repository.suggestion.SuggestionRepository;
import org.rocs.osdrmsa.utils.ai.AiAnalysisClient;
import org.rocs.osdrmsa.utils.ai.OllamaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DocumentAiProcessor {

    private static final Logger log = LoggerFactory.getLogger(DocumentAiProcessor.class);

    private static final List<String> IMAGE_EXTENSIONS =
            List.of("jpg", "jpeg", "png", "bmp", "tif", "tiff", "gif");

    private static final int MAX_RECENT_RECORDS = 10;
    private static final int MAX_RETRIEVED_HINTS = 3;

    private static final String SYSTEM_PROMPT = """
            You are the AI Support Module inside the Rogationist College Office for Student Discipline \
            system. Your job is to write a short, neutral, informational note that helps a Prefect review \
            an appeal letter a student just submitted -- you are NOT deciding the appeal and must never \
            state or imply what the outcome should be.

            Rules you must follow:
            1. Base your note only on the LETTER TEXT, CASE HISTORY, and POLICY HINTS given to you below. \
            Never invent offenses, dates, or facts that are not present in that information.
            2. Do not approve, deny, or recommend a specific disciplinary action. Do not use words like \
            "should be approved" or "should be denied."
            3. If the case history shows repeat violations, or the letter lacks supporting detail, you may \
            neutrally point that out as something worth the Prefect's attention -- but always as an \
            observation, not a conclusion.
            4. Keep the note to 2-4 sentences, plain language, no headers or bullet points.
            5. If the letter text is too short or unclear to say anything useful, say so plainly instead of \
            guessing.""";

    private final DocumentRepository documentRepository;
    private final RecordRepository recordRepository;
    private final SuggestionRepository suggestionRepository;
    private final GeneratedSuggestionRepository generatedSuggestionRepository;
    private final AiAnalysisClient aiAnalysisClient;
    private final OllamaClient ollamaClient;
    private final TransactionTemplate transactionTemplate;

    private final Tika tika = new Tika();

    public DocumentAiProcessor(
            DocumentRepository documentRepository,
            RecordRepository recordRepository,
            SuggestionRepository suggestionRepository,
            GeneratedSuggestionRepository generatedSuggestionRepository,
            AiAnalysisClient aiAnalysisClient,
            OllamaClient ollamaClient,
            PlatformTransactionManager transactionManager) {
        this.documentRepository = documentRepository;
        this.recordRepository = recordRepository;
        this.suggestionRepository = suggestionRepository;
        this.generatedSuggestionRepository = generatedSuggestionRepository;
        this.aiAnalysisClient = aiAnalysisClient;
        this.ollamaClient = ollamaClient;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Async("aiTaskExecutor")
    public void process(Long documentId) {
        Document document = documentRepository.findById(documentId).orElse(null);
        if (document == null || document.getFileData() == null) {
            return;
        }

        String extractedText;
        try {
            extractedText = extractText(document.getFileData(), document.getFileName());
        } catch (Exception e) {
            log.warn("Text extraction failed for document {}: {}", documentId, e.getMessage());
            return;
        }

        document.setExtractedText(extractedText);
        document = documentRepository.save(document);

        generateCaseSuggestion(document, document.getStudent(), extractedText);
    }

    private String generateCaseSuggestion(Document document, Student student, String extractedText) {
        if (extractedText == null || extractedText.isBlank()) {
            return null;
        }

        try {
            String[] gathered = transactionTemplate.execute(status -> new String[] {
                    buildCaseHistorySummary(student),
                    buildRetrievedHints(extractedText)
            });
            String caseHistory = gathered[0];
            String policyHints = gathered[1];

            String userPrompt = "LETTER TEXT:\n" + extractedText.trim() +
                    "\n\n" + caseHistory +
                    "\n\n" + policyHints;

            List<ChatMessageDto> messages = List.of(
                    new ChatMessageDto("system", SYSTEM_PROMPT),
                    new ChatMessageDto("user", userPrompt)
            );

            String generatedText = ollamaClient.chat(messages);

            GeneratedSuggestion generated = new GeneratedSuggestion();
            generated.setDocument(document);
            generated.setGeneratedText(generatedText);
            generated.setGeneratedAt(LocalDateTime.now());
            generatedSuggestionRepository.save(generated);

            return generatedText;
        } catch (Exception e) {
            log.warn("AI Support Module suggestion generation failed for document {}: {}",
                    document.getDocumentId(), e.getMessage());
            return null;
        }
    }

    private String buildCaseHistorySummary(Student student) {
        List<Record> records = recordRepository.findByEnrollmentStudentStudentId(student.getStudentId());

        StringBuilder sb = new StringBuilder("CASE HISTORY (");
        sb.append(records.size()).append(" total violation");
        sb.append(records.size() == 1 ? "" : "s");
        if (records.size() > MAX_RECENT_RECORDS) {
            sb.append(", showing ").append(MAX_RECENT_RECORDS).append(" most recent");
        }
        sb.append(" on file):\n");

        if (records.isEmpty()) {
            sb.append("- No prior disciplinary records on file. This would be the student's first.\n");
            return sb.toString();
        }

        records.stream()
                .sorted(Comparator.comparing(Record::getDateOfViolation, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(MAX_RECENT_RECORDS)
                .forEach(r -> sb.append("- ")
                        .append(r.getOffense() != null ? r.getOffense().getOffense() : "Unknown offense")
                        .append(", filed ").append(r.getDateOfViolation())
                        .append(", status ").append(r.getStatus())
                        .append("\n"));

        return sb.toString();
    }

    private String buildRetrievedHints(String extractedText) {
        List<Suggestion> allSuggestions = suggestionRepository.findAll();
        if (allSuggestions.isEmpty()) {
            return "POLICY HINTS: none available.";
        }

        List<AiAnalysisClient.SuggestionCandidate> candidates = allSuggestions.stream()
                .map(s -> new AiAnalysisClient.SuggestionCandidate(s.getSuggestionId(), s.getSuggestionText()))
                .toList();

        AiAnalysisClient.AnalyzeResult result = aiAnalysisClient.analyze(extractedText, candidates);

        Map<Long, Suggestion> byId = allSuggestions.stream()
                .collect(Collectors.toMap(Suggestion::getSuggestionId, s -> s, (a, b) -> a, HashMap::new));

        List<String> hints = new ArrayList<>();
        result.matches().stream()
                .limit(MAX_RETRIEVED_HINTS)
                .forEach(match -> {
                    Suggestion suggestion = byId.get(match.suggestionId());
                    if (suggestion != null) {
                        hints.add("- " + suggestion.getSuggestionText());
                    }
                });

        if (hints.isEmpty()) {
            return "POLICY HINTS: none matched this letter closely enough to be useful.";
        }

        return "POLICY HINTS (general guidance, not the full Student Handbook):\n" + String.join("\n", hints);
    }

    private String extractText(byte[] fileBytes, String filename) {
        String extension = extension(filename);

        if (IMAGE_EXTENSIONS.contains(extension)) {
            return aiAnalysisClient.ocr(fileBytes, filename);
        }

        try {
            return tika.parseToString(new ByteArrayInputStream(fileBytes));
        } catch (Exception e) {
            throw new IllegalStateException("Could not extract text from the uploaded file.", e);
        }
    }

    private String extension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }
}
