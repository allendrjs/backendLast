package org.rocs.osdrmsa.domain.record;

import jakarta.persistence.*;
import lombok.Data;
import org.rocs.osdrmsa.domain.disciplinary.action.DisciplinaryAction;
import org.rocs.osdrmsa.domain.enrollment.Enrollment;
import org.rocs.osdrmsa.domain.offense.Offense;
import org.rocs.osdrmsa.domain.person.employee.Employee;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "RECORD")
public class Record {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RECORDID")
    private Long recordId;

    @ManyToOne
    @JoinColumn(name = "ENROLLMENTID")
    private Enrollment enrollment;

    @ManyToOne
    @JoinColumn(name = "EMPLOYEEID")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "OFFENSEID")
    private Offense offense;

    @Column(name = "DATEOFVIOLATION")
    private LocalDate dateOfViolation;

    @ManyToOne
    @JoinColumn(name = "ACTIONID")
    private DisciplinaryAction action;

    @Column(name = "DATEOFRESOLUTION", columnDefinition = "DATE")
    private LocalDateTime dateOfResolution;

    @Column(name = "REMARKS")
    private String remarks;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private RecordStatus status;
}
