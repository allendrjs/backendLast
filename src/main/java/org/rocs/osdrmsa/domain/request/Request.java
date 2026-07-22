package org.rocs.osdrmsa.domain.request;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REQUEST ID", nullable = false, updatable = false)
    private long requestID;

    @Column(name = "EMPLOYEE ID", nullable = false)
    private String employeeID;

    @Column(name = "DETAILS", nullable = false)
    private String details;

    @Column(name = "MESSAGE", nullable = false)
    private String message;

    @Column(name = "TYPE", nullable = false)
    private String type;

    @Column(name = "STATUS", nullable = false)
    private RequestStatus status;

    @Column(name = "DATE OF PROCESS", nullable = false)
    private Date dateProcessed;

    @Column(name = "REMARKS", nullable = false)
    private String remarks;

}
