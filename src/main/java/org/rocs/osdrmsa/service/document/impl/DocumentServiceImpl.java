package org.rocs.osdrmsa.service.document.impl;

import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.rocs.osdrmsa.domain.document.Document;
import org.rocs.osdrmsa.domain.login.Login;
import org.rocs.osdrmsa.domain.person.student.Student;
import org.rocs.osdrmsa.domain.record.Record;
import org.rocs.osdrmsa.domain.suggestion.GeneratedSuggestion;
import org.rocs.osdrmsa.domain.suggestion.Suggestion;
import org.rocs.osdrmsa.dto.response.DocumentUploadResponse;
import org.rocs.osdrmsa.dto.summary.ChatMessageDto;
import org.rocs.osdrmsa.repository.document.DocumentRepository;
import org.rocs.osdrmsa.repository.login.LoginRepository;
import org.rocs.osdrmsa.repository.record.RecordRepository;
import org.rocs.osdrmsa.repository.student.StudentRepository;
import org.rocs.osdrmsa.repository.suggestion.GeneratedSuggestionRepository;
import org.rocs.osdrmsa.repository.suggestion.SuggestionRepository;
import org.rocs.osdrmsa.service.document.DocumentAiProcessor;
import org.rocs.osdrmsa.service.document.DocumentService;
import org.rocs.osdrmsa.utils.ai.AiAnalysisClient;
import org.rocs.osdrmsa.utils.ai.OllamaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentServiceImpl.class);

    private final LoginRepository loginRepository;
    private final StudentRepository studentRepository;
    private final DocumentRepository documentRepository;
    private final DocumentAiProcessor documentAiProcessor;

    @Override
    public DocumentUploadResponse processAppealUpload(
            String username, byte[] fileBytes, String filename, String contentType) {
        if (fileBytes == null || fileBytes.length == 0) {
            throw new IllegalArgumentException("Uploaded file is empty.");
        }

        Student student = resolveStudent(username);

        Document document = new Document();
        document.setStudent(student);
        document.setFileName(filename);
        document.setContentType(contentType);
        document.setFileData(fileBytes);
        document = documentRepository.save(document);

        documentAiProcessor.process(document.getDocumentId());

        return new DocumentUploadResponse(document.getDocumentId(), null, null);
    }

    @Override
    public Document getById(Long documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new NoSuchElementException("Document not found: " + documentId));
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
}
