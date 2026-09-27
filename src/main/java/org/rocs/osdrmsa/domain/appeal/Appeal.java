package org.rocs.osdrmsa.domain.appeal;

import jakarta.persistence.*;
import lombok.Data;
import org.rocs.osdrmsa.domain.document.Document;
import org.rocs.osdrmsa.domain.enrollment.Enrollment;
import org.rocs.osdrmsa.domain.record.Record;

import java.time.LocalDate;

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

    @Column(name = "DATEFILED")
    private LocalDate dateFiled;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "DATEPROCESSED")
    private LocalDate dateProcessed;

    @Column(name = "REMARKS")
    private String remarks;

    @Column(name = "AIRECOMMENDATION")
    private String aiRecommendation;

    @Lob
    @Column(name = "AIREASONING")
    private String aiReasoning;
}
