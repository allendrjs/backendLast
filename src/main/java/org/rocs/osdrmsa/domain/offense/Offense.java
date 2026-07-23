package org.rocs.osdrmsa.domain.offense;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Offense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "offenseID", nullable = false, updatable = false)
    private long offenseId;

    @Column(name = "offense")
    private String offense;

    @Column(name = "type")
    private String type;

    @Column(name = "description")
    private String description;

}
