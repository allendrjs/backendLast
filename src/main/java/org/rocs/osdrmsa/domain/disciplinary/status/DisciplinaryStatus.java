package org.rocs.osdrmsa.domain.disciplinary.status;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class DisciplinaryStatus {

    @Id
    @Column(name = "disciplinaryStatusID", nullable = false, updatable = false)
    private long disciplinaryStatusId;

    @Column(name = "status")
    private String status;

    @Column(name = "description")
    private String description;

}
