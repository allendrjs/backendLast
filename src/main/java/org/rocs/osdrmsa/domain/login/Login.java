package org.rocs.osdrmsa.domain.login;

import jakarta.persistence.*;
import lombok.Data;
import org.rocs.osdrmsa.domain.person.Person;
import org.rocs.osdrmsa.domain.person.employee.Employee;

import java.util.Date;

@Entity
@Data
public class Login {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOGINID")
    private Long id;

    @Column(name = "USERNAME", nullable = false, unique = true)
    private String username;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "JOIN_DATE", nullable = false, updatable = false)
    private Date joinDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_LOGIN_DATE")
    private Date lastLoginDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE", nullable = false)
    private Role role;

    @Column(name = "AUTHORITIES")
    private String authorities;

    @Column(name = "IS_ACTIVE", nullable = false)
    private boolean active = true;

    @Column(name = "IS_LOCKED", nullable = false)
    private boolean locked = false;

    @OneToOne
    @JoinColumn(name = "personID")
    private Person person;

    @Transient
    private Employee employee;

    @PrePersist
    protected void onCreate() {
        if (joinDate == null) {
            joinDate = new Date();
        }
    }
}
