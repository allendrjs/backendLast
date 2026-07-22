package org.rocs.osdrmsa.domain.disciplinary.action;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class DisciplinaryAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ACTION ID", nullable = false, updatable = false)
    private long actionId;

    @Column(name = "ACTION NAME", nullable = false)
    private String actionName;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

}
