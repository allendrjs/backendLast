package org.rocs.osdrmsa.domain.disciplinary.status;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class DisciplinaryStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DISCIPLINARY STATUS ID", nullable = false, updatable = false)
    private long disciplinaryStatusId;

    @Column(name = "STATUS", nullable = false)
    private String status;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

}
