package org.rocs.osdrmsa.domain.person;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.Connection;

@Entity
@Data
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PERSON ID", nullable = false, updatable = false)
    private long personID;
    @Column(name = "LASTNAME", nullable = false)
    private String lastName;
    @Column(name = "FIRSTNAME", nullable = false)
    private String firstName;
    @Column(name = "MIDDLENAME", nullable = false)
    private String middleName;

    @Bean
    CommandLineRunner checkConnection(DataSource dataSource) {
        return args -> {
            try (Connection conn = dataSource.getConnection()) {
                System.out.println("User: " + conn.getMetaData().getUserName());
                System.out.println("URL : " + conn.getMetaData().getURL());
            }
        };
    }
}
