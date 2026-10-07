package org.rocs.osdrmsa.domain.appeal;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.rocs.osdrmsa.domain.document.Document;
import org.rocs.osdrmsa.domain.enrollment.Enrollment;
import org.rocs.osdrmsa.domain.record.Record;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "APPEAL")
public class Appeal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "APPEALID")
    private Long appealId;

    @ManyToOne
    @JoinColumn(name = "RECORDID")
    private Record record;

    @ManyToOne
    @JoinColumn(name = "ENROLLMENTID")
    private Enrollment enrollment;

    @ManyToOne
    @JoinColumn(name = "DOCUMENTID")
    private Document document;

    @Column(name = "MESSAGE")
    private String message;

    @Column(name = "DATEFILED", columnDefinition = "DATE")
    private LocalDateTime dateFiled;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "DATEPROCESSED", columnDefinition = "DATE")
    private LocalDateTime dateProcessed;

    @Column(name = "REMARKS")
    private String remarks;

    @Column(name = "AIRECOMMENDATION")
    private String aiRecommendation;

    @Lob
    @Column(name = "AIREASONING")
    private String aiReasoning;

    @JdbcTypeCode(SqlTypes.INTEGER)
    @Column(name = "EDITED", nullable = false)
    private boolean edited = false;

    @Column(name = "EDITEDAT", columnDefinition = "DATE")
    private LocalDateTime editedAt;
}
