package org.rocs.osdrmsa.domain.request;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
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

    @Column(name = "DELIVERYMETHOD", nullable = false)
    private String deliveryMethod = "HARDCOPY";

    @Column(name = "DELIVERYEMAIL", length = 254)
    private String deliveryEmail;

    @Column(name = "DATEFILED", nullable = false, columnDefinition = "DATE")
    private LocalDateTime dateFiled;

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