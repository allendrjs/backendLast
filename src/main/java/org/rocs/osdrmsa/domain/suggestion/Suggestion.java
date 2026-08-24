package org.rocs.osdrmsa.domain.suggestion;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "SUGGESTION")
public class Suggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SUGGESTIONID")
    private Long suggestionId;

    @Column(name = "TYPE")
    private String type;

    @Lob
    @Column(name = "SUGGESTIONTEXT")
    private String suggestionText;
}
