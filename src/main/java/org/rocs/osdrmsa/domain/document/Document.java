package org.rocs.osdrmsa.domain.document;

import jakarta.persistence.*;
import lombok.Data;
import org.rocs.osdrmsa.domain.person.student.Student;

@Entity
@Data
@Table(name = "DOCUMENT")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DOCUMENTID")
    private Long documentId;

    @ManyToOne
    @JoinColumn(name = "STUDENTID")
    private Student student;

    @Lob
    @Column(name = "EXTRACTEDTEXT")
    private String extractedText;
}
