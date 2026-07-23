package org.rocs.osdrmsa;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// Boots the full Spring context against a real Oracle DB - requires a live
// database (see docker/local setup docs). Tagged so CI can skip it while
// everything else still runs; local `./gradlew build` still runs it by default.
// "test" is in JwtService's SECRET_EXEMPT_PROFILES, so this activates that
// exemption instead of requiring a real JWT_SECRET env var just to run tests.
@Tag("requires-db")
@ActiveProfiles("test")
@SpringBootTest
class OsdrmsaApplApplicationTests {

	@Test
	void contextLoads() {
	}

}
