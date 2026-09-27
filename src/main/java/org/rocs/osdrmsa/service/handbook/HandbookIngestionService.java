package org.rocs.osdrmsa.service.handbook;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.rocs.osdrmsa.repository.handbook.HandbookChunkRepository;
import org.rocs.osdrmsa.utils.ai.OllamaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

/**
 * One-time (re-runnable) loader that embeds the bundled, pre-chunked Student Handbook
 * text (src/main/resources/handbooks/handbook_chunks.json) and stores it, with vector
 * embeddings, in the handbook_chunk table. Triggered manually via
 * POST /api/admin/handbook/ingest rather than on every app startup, since embedding
 * ~700+ chunks through a local CPU-only Ollama instance takes real time.
 */
@Service
public class HandbookIngestionService {

    private static final Logger log = LoggerFactory.getLogger(HandbookIngestionService.class);
    private static final Set<String> VALID_DEPARTMENTS = Set.of("JHS", "SHS", "COLLEGE");

    private final HandbookChunkRepository handbookChunkRepository;
    private final OllamaClient ollamaClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public HandbookIngestionService(HandbookChunkRepository handbookChunkRepository, OllamaClient ollamaClient) {
        this.handbookChunkRepository = handbookChunkRepository;
        this.ollamaClient = ollamaClient;
    }

    public record IngestResult(int totalChunks, int perDepartment) {
    }

    public record ChunkSeed(
            @JsonProperty("department") String department,
            @JsonProperty("section_title") String sectionTitle,
            @JsonProperty("content") String content
    ) {
    }

    public String ingestAll() {
        List<ChunkSeed> seeds = loadSeeds();

        for (String department : VALID_DEPARTMENTS) {
            handbookChunkRepository.deleteByDepartment(department);
        }

        int processed = 0;
        int failed = 0;
        for (ChunkSeed seed : seeds) {
            if (!VALID_DEPARTMENTS.contains(seed.department())) {
                continue;
            }
            try {
                float[] embedding = ollamaClient.embed(seed.content());
                handbookChunkRepository.insert(seed.department(), seed.sectionTitle(), seed.content(), embedding);
                processed++;
            } catch (Exception e) {
                failed++;
                log.warn("Failed to embed/store handbook chunk ({}, \"{}\"): {}",
                        seed.department(), seed.sectionTitle(), e.getMessage());
            }
        }

        return "Ingested " + processed + " handbook chunks (" + failed + " failed). " +
                "JHS=" + handbookChunkRepository.countByDepartment("JHS") +
                ", SHS=" + handbookChunkRepository.countByDepartment("SHS") +
                ", COLLEGE=" + handbookChunkRepository.countByDepartment("COLLEGE");
    }

    private List<ChunkSeed> loadSeeds() {
        try (InputStream in = new ClassPathResource("handbooks/handbook_chunks.json").getInputStream()) {
            return objectMapper.readValue(in, objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, ChunkSeed.class));
        } catch (Exception e) {
            throw new IllegalStateException("Could not load bundled handbook_chunks.json", e);
        }
    }
}