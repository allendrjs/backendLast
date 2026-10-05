package org.rocs.osdrmsa.domain.academicperiod;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "ACADEMIC_PERIOD")
public class AcademicPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PERIODID")
    private Long periodId;

    @Column(name = "LABEL", nullable = false)
    private String label;

    @Column(name = "STARTDATE")
    private LocalDate startDate;

    @Column(name = "ENDDATE")
    private LocalDate endDate;

    @Column(name = "APPEALDEADLINE")
    private LocalDate appealDeadline;

    @JdbcTypeCode(SqlTypes.INTEGER)
    @Column(name = "ACTIVE", nullable = false)
    private boolean active = false;
}
