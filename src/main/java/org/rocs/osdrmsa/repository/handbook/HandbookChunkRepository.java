package org.rocs.osdrmsa.repository.handbook;

import org.rocs.osdrmsa.domain.handbook.HandbookChunk;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Stores and retrieves Student Handbook text chunks and their embeddings, using Oracle
 * Database 23ai's native VECTOR type and VECTOR_DISTANCE similarity search. Deliberately
 * plain JDBC rather than JPA: the VECTOR column is written/read as an inline TO_VECTOR(...)
 * literal built from the embedding's own float values, which avoids depending on ORM/driver
 * support for binding oracle.sql.VECTOR directly.
 */
@Repository
public class HandbookChunkRepository {

    private final JdbcTemplate jdbcTemplate;

    public HandbookChunkRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void deleteByDepartment(String department) {
        jdbcTemplate.update("DELETE FROM handbook_chunk WHERE department = ?", department);
    }

    public void insert(String department, String sectionTitle, String content, float[] embedding) {
        String vectorLiteral = toVectorLiteral(embedding);
        jdbcTemplate.update(
                "INSERT INTO handbook_chunk (department, section_title, content, embedding) " +
                        "VALUES (?, ?, ?, TO_VECTOR(" + vectorLiteral + "))",
                department, sectionTitle, content
        );
    }

    public List<HandbookChunk> findNearest(String department, float[] queryEmbedding, int limit) {
        String vectorLiteral = toVectorLiteral(queryEmbedding);
        String sql = "SELECT chunk_id, department, section_title, content " +
                "FROM handbook_chunk " +
                "WHERE department = ? " +
                "ORDER BY VECTOR_DISTANCE(embedding, TO_VECTOR(" + vectorLiteral + "), COSINE) " +
                "FETCH FIRST ? ROWS ONLY";

        return jdbcTemplate.query(sql, (rs, rowNum) -> new HandbookChunk(
                rs.getLong("chunk_id"),
                rs.getString("department"),
                rs.getString("section_title"),
                rs.getString("content")
        ), department, limit);
    }

    public int countByDepartment(String department) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM handbook_chunk WHERE department = ?", Integer.class, department);
        return count == null ? 0 : count;
    }

    /**
     * All chunks for a department in original document order (insertion/chunk_id order),
     * for reconstructing full sections to browse -- as opposed to findNearest(), which is
     * for RAG similarity search.
     */
    public List<HandbookChunk> findAllByDepartmentOrdered(String department) {
        return jdbcTemplate.query(
                "SELECT chunk_id, department, section_title, content " +
                        "FROM handbook_chunk WHERE department = ? ORDER BY chunk_id",
                (rs, rowNum) -> new HandbookChunk(
                        rs.getLong("chunk_id"),
                        rs.getString("department"),
                        rs.getString("section_title"),
                        rs.getString("content")
                ), department);
    }

    private String toVectorLiteral(float[] values) {
        String joined = IntStream.range(0, values.length)
                .mapToObj(i -> Float.toString(values[i]))
                .collect(Collectors.joining(","));
        return "'[" + joined + "]'";
    }
}
