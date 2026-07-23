package org.rocs.osdrmsa.domain.appeal;

import jakarta.persistence.*;
import lombok.Data;
import org.rocs.osdrmsa.domain.enrollment.Enrollment;
import org.rocs.osdrmsa.domain.record.Record;

import java.util.Date;

@Entity
@Data
public class Appeal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "APPEAL ID", nullable = false, updatable = false)
    private long appealID;

    @ManyToOne
    @JoinColumn(name = "recordID", nullable = false)
    private Record record;

    @ManyToOne
    @JoinColumn(name = "enrollmentID", nullable = false)
    private Enrollment enrollment;

    @Column(name = "MESSAGE", nullable = false)
    private String message;

    @Column(name = "DATE FILED", nullable = false)
    private Date dateFiled;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private AppealStatus status;

    @Column(name = "DATE PROCESSED")
    private Date dateProcessed;

    @Column(name = "REMARKS")
    private String remarks;
}
