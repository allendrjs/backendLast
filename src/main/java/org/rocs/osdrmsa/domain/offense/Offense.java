package org.rocs.osdrmsa.domain.offense;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Offense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OFFENSE ID", nullable = false, updatable = false)
    private long offenseId;

    @Column(name = "OFFENSE", nullable = false)
    private String offense;

    @Column(name = "OFFENSE TYPE", nullable = false)
    private String type;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

}
