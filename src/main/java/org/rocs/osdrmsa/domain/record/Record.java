package org.rocs.osdrmsa.domain.record;

import jakarta.persistence.*;
import lombok.Data;
import org.rocs.osdrmsa.domain.disciplinary.action.DisciplinaryAction;
import org.rocs.osdrmsa.domain.enrollment.Enrollment;
import org.rocs.osdrmsa.domain.offense.Offense;
import org.rocs.osdrmsa.domain.person.employee.Employee;

import java.util.Date;

@Entity
@Data
public class Record {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recordID", nullable = false, updatable = false)
    private long recordId;

    @ManyToOne
    @JoinColumn(name = "enrollmentID", nullable = false)
    private Enrollment enrollment;

    @ManyToOne
    @JoinColumn(name = "employeeID", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "offenseID", nullable = false)
    private Offense offense;

    @Temporal(TemporalType.DATE)
    @Column(name = "dateOfViolation", nullable = false)
    private Date dateOfViolation;

    @ManyToOne
    @JoinColumn(name = "actionID", nullable = false)
    private DisciplinaryAction action;

    @Temporal(TemporalType.DATE)
    @Column(name = "dateOfResolution")
    private Date dateOfResolution;

    @Column(name = "remarks")
    private String remarks;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RecordStatus status;

}
