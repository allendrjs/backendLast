package org.rocs.osdrmsa.service.ai;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.handbook.HandbookChunk;
import org.rocs.osdrmsa.dto.summary.ChatMessageDto;
import org.rocs.osdrmsa.repository.handbook.HandbookChunkRepository;
import org.rocs.osdrmsa.utils.ai.OllamaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Gives a prefect/dept head reviewing an Appeal or a Request a quick, handbook-grounded
 * recommendation (APPROVABLE / DENIABLE / UNCERTAIN) plus a short reasoning, using the same
 * Ollama + Student Handbook RAG setup already built for the mobile chatbot. This is only a
 * suggestion for the human reviewer -- it never makes the actual approve/deny decision.
 */
@Service
@RequiredArgsConstructor
public class AiCaseAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(AiCaseAnalysisService.class);

    private static final String SYSTEM_PROMPT_TEMPLATE = """
            You are the AI Support Module inside the Rogationist College Office for Student Discipline system.
            A prefect or department head is reviewing a %s. Your job is to give them a quick,
            handbook-grounded recommendation to help them decide -- you do NOT make the final decision,
            they do.

            Rules:
            1. Only use the CASE CONTEXT and HANDBOOK EXCERPTS provided below. Never invent facts,
               offenses, dates, or policies that are not present in those blocks.
            2. Respond in EXACTLY this format, with no extra text before or after:
               RECOMMENDATION: <one of APPROVABLE, DENIABLE, UNCERTAIN>
               REASONING: <1 to 3 short sentences citing the relevant handbook section or case facts>
            3. Use APPROVABLE when the case facts and handbook support granting it, DENIABLE when
               they support rejecting it, and UNCERTAIN only when the handbook excerpts genuinely
               don't cover this situation.
            4. Keep REASONING concise and in plain language -- this is a suggestion for a human
               reviewer, not a final ruling.
            """;

    private static final Pattern RESPONSE_PATTERN = Pattern.compile(
            "RECOMMENDATION:\\s*(APPROVABLE|DENIABLE|UNCERTAIN)\\s*REASONING:\\s*(.+)",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    private static final int HANDBOOK_TOP_K_DEFAULT = 5;

    private final OllamaClient ollamaClient;
    private final HandbookChunkRepository handbookChunkRepository;

    @Value("${handbook.top-k:5}")
    private int handbookTopK = HANDBOOK_TOP_K_DEFAULT;

    public record Result(String recommendation, String reasoning) {
    }

    public Result analyze(String caseKind, String department, String contextText) {
        try {
            String handbookExcerpts = buildHandbookExcerpts(department, contextText);
            String systemPrompt = String.format(SYSTEM_PROMPT_TEMPLATE, caseKind);
            String userMessage = contextText + "\n\n" + handbookExcerpts;

            String reply = ollamaClient.chat(List.of(
                    new ChatMessageDto("system", systemPrompt),
                    new ChatMessageDto("user", userMessage)
            ));

            return parse(reply);
        } catch (Exception e) {
            log.warn("AI case analysis failed for a {}: {}", caseKind, e.getMessage());
            return new Result("UNCERTAIN", "AI analysis is temporarily unavailable.");
        }
    }

    private String buildHandbookExcerpts(String department, String queryText) {
        if (department == null) {
            return "HANDBOOK EXCERPTS: unavailable (no department on file for this case).";
        }

        try {
            float[] queryEmbedding = ollamaClient.embed(queryText);
            List<HandbookChunk> matches = handbookChunkRepository.findNearest(department, queryEmbedding, handbookTopK);

            if (matches.isEmpty()) {
                return "HANDBOOK EXCERPTS: none available for department " + department + ".";
            }

            StringBuilder sb = new StringBuilder("HANDBOOK EXCERPTS (from the ")
                    .append(department).append(" Student Handbook):\n");
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

    private Result parse(String reply) {
        if (reply == null || reply.isBlank()) {
            return new Result("UNCERTAIN", "AI did not return a response.");
        }

        Matcher matcher = RESPONSE_PATTERN.matcher(reply.trim());
        if (matcher.find()) {
            String recommendation = matcher.group(1).trim().toUpperCase();
            String reasoning = matcher.group(2).trim();
            return new Result(recommendation, reasoning);
        }

        // Couldn't parse the strict format -- fall back to keeping the raw reply as reasoning
        // rather than dropping the AI's answer entirely.
        return new Result("UNCERTAIN", reply.trim());
    }
}
