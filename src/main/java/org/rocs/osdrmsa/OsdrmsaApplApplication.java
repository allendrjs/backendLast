package org.rocs.osdrmsa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class OsdrmsaApplApplication {

	public static void main(String[] args) {
		SpringApplication.run(OsdrmsaApplApplication.class, args);

		PasswordEncoder encoder = new BCryptPasswordEncoder();

		String password = "admin123";

		String encodedPassword = encoder.encode(password);

		System.out.println("Original password: " + password);
		System.out.println("BCrypt password: " + encodedPassword);
	}

}
