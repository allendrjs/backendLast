package org.rocs.osdrmsa.repository.handbook;

import oracle.sql.VECTOR;
import org.rocs.osdrmsa.domain.handbook.HandbookChunk;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;

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
        try {
            VECTOR vector = VECTOR.ofFloat32Values(embedding);
            jdbcTemplate.update(
                    "INSERT INTO handbook_chunk (department, section_title, content, embedding) " +
                            "VALUES (?, ?, ?, ?)",
                    department, sectionTitle, content, vector
            );
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to encode embedding vector.", e);
        }
    }

    public List<HandbookChunk> findNearest(String department, float[] queryEmbedding, int limit) {
        try {
            VECTOR vector = VECTOR.ofFloat32Values(queryEmbedding);
            String sql = "SELECT chunk_id, department, section_title, content " +
                    "FROM handbook_chunk " +
                    "WHERE department = ? " +
                    "ORDER BY VECTOR_DISTANCE(embedding, ?, COSINE) " +
                    "FETCH FIRST ? ROWS ONLY";

            return jdbcTemplate.query(sql, (rs, rowNum) -> new HandbookChunk(
                    rs.getLong("chunk_id"),
                    rs.getString("department"),
                    rs.getString("section_title"),
                    rs.getString("content")
            ), department, vector, limit);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to encode embedding vector.", e);
        }
    }

    public int countByDepartment(String department) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM handbook_chunk WHERE department = ?", Integer.class, department);
        return count == null ? 0 : count;
    }

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
}