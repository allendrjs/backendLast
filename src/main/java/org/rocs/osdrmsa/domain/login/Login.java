package org.rocs.osdrmsa.domain.login;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.rocs.osdrmsa.domain.person.Person;
import org.rocs.osdrmsa.domain.person.employee.Employee;

import java.util.Date;
import org.rocs.osdrmsa.utils.security.enumeration.Role;

@Entity
@Data
public class Login {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "join_date", nullable = false, updatable = false)
    private Date joinDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_login_date")
    private Date lastLoginDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "authorities")
    private String authorities;

    @JdbcTypeCode(SqlTypes.INTEGER)
    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @JdbcTypeCode(SqlTypes.INTEGER)
    @Column(name = "is_locked", nullable = false)
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