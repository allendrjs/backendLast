package org.rocs.osdrmsa.domain.request;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@Table(name = "REQUEST")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REQUESTID", nullable = false, updatable = false)
    private long requestID;

    @Column(name = "EMPLOYEEID", nullable = false)
    private String employeeID;

    @Column(name = "DETAILS", nullable = false)
    private String details;

    @Column(name = "MESSAGE", nullable = false)
    private String message;

    @Column(name = "TYPE", nullable = false)
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private RequestStatus status;

    @Column(name = "DATEFILED", nullable = false)
    private LocalDate dateFiled;

    @Column(name = "DATEPROCESSED")
    private Date dateProcessed;

    @Lob
    @Column(name = "AIRESPONSE")
    private String aiResponse;

    @Column(name = "AIRECOMMENDATION")
    private String aiRecommendation;

    @Lob
    @Column(name = "AIREASONING")
    private String aiReasoning;

    @Column(name = "REMARKS")
    private String remarks;
}