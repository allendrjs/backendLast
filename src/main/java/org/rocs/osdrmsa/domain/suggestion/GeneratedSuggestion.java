package org.rocs.osdrmsa.domain.suggestion;

import jakarta.persistence.*;
import lombok.Data;
import org.rocs.osdrmsa.domain.document.Document;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "GENERATEDSUGGESTION")
public class GeneratedSuggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GENERATEDSUGGESTIONID")
    private Long generatedSuggestionId;

    @ManyToOne
    @JoinColumn(name = "SUGGESTIONID")
    private Suggestion suggestion;

    @ManyToOne
    @JoinColumn(name = "DOCUMENTID")
    private Document document;

    @Lob
    @Column(name = "GENERATEDTEXT")
    private String generatedText;

    @Column(name = "GENERATEDAT")
    private LocalDateTime generatedAt;
}
