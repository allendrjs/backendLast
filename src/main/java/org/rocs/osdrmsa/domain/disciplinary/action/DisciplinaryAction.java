package org.rocs.osdrmsa.domain.disciplinary.action;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class DisciplinaryAction {

    @Id
    @Column(name = "actionID", nullable = false, updatable = false)
    private long actionId;

    @Column(name = "action")
    private String actionName;

    @Column(name = "description")
    private String description;

}
