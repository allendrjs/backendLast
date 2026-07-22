package org.rocs.osdrmsa.domain.login;

import jakarta.persistence.*;
import lombok.Data;
import org.rocs.osdrmsa.domain.person.Person;
import org.rocs.osdrmsa.domain.person.employee.Employee;

@Entity
@Data
public class Login {

    @Id
    @Column(name = "LOGINID")
    private Long id;

    @Column(name = "USERNAME", nullable = false, unique = true)
    private String username;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @OneToOne
    @JoinColumn(name = "personID")
    private Person person;

    @Transient
    private Employee employee;
}
