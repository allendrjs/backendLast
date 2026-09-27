package org.rocs.osdrmsa.service.handbook.impl;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.enrollment.Enrollment;
import org.rocs.osdrmsa.domain.handbook.HandbookChunk;
import org.rocs.osdrmsa.domain.login.Login;
import org.rocs.osdrmsa.domain.person.student.Student;
import org.rocs.osdrmsa.dto.response.HandbookResponse;
import org.rocs.osdrmsa.dto.response.HandbookSectionResponse;
import org.rocs.osdrmsa.repository.enrollment.EnrollmentRepository;
import org.rocs.osdrmsa.repository.handbook.HandbookChunkRepository;
import org.rocs.osdrmsa.repository.login.LoginRepository;
import org.rocs.osdrmsa.repository.student.StudentRepository;
import org.rocs.osdrmsa.service.handbook.HandbookService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Reconstructs the full, readable Student Handbook (scoped to the student's own department)
 * from the small chunks stored for RAG retrieval, by merging consecutive chunks that share
 * the same section title back into one section -- for browsing, as opposed to ChatServiceImpl's
 * top-k similarity search used for chat answers.
 */
@Service
@RequiredArgsConstructor
public class HandbookServiceImpl implements HandbookService {

    private final LoginRepository loginRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final HandbookChunkRepository handbookChunkRepository;

    @Override
    public HandbookResponse getHandbook(String username) {
        Student student = resolveStudent(username);

        Enrollment enrollment = enrollmentRepository
                .findTopByStudentStudentIdOrderBySchoolYearDesc(student.getStudentId())
                .orElse(null);

        if (enrollment == null || enrollment.getDepartment() == null) {
            throw new IllegalStateException(
                    "No enrollment/department on file, so the Student Handbook can't be shown yet.");
        }

        String department = enrollment.getDepartment().name();
        List<HandbookChunk> chunks = handbookChunkRepository.findAllByDepartmentOrdered(department);

        return new HandbookResponse(department, mergeIntoSections(chunks));
    }

    private List<HandbookSectionResponse> mergeIntoSections(List<HandbookChunk> chunks) {
        List<HandbookSectionResponse> sections = new ArrayList<>();
        if (chunks.isEmpty()) {
            return sections;
        }

        String currentTitle = null;
        StringBuilder currentContent = null;

        for (HandbookChunk chunk : chunks) {
            String title = chunk.sectionTitle();
            boolean sameSection = currentTitle != null
                    && ((title == null && currentTitle == null)
                        || (title != null && title.equals(currentTitle)));

            if (sameSection) {
                currentContent.append(' ').append(chunk.content());
            } else {
                if (currentTitle != null) {
                    sections.add(new HandbookSectionResponse(currentTitle, currentContent.toString().trim()));
                }
                currentTitle = title;
                currentContent = new StringBuilder(chunk.content());
            }
        }

        if (currentTitle != null) {
            sections.add(new HandbookSectionResponse(currentTitle, currentContent.toString().trim()));
        }

        return sections;
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
