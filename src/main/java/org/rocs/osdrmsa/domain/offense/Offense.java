package org.rocs.osdrmsa.domain.offense;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import lombok.Data;

@Entity
@Data
@Table(name = "OFFENSE")
public class Offense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OFFENSEID")
    private Long offenseId;

    @Column(name = "OFFENSE")
    private String offense;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "ISACTIVE", nullable = false)
    @JdbcTypeCode(SqlTypes.INTEGER)
    private Boolean isActive = true;
}