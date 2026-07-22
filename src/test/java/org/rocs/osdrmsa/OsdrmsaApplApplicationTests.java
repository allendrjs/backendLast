package org.rocs.osdrmsa;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// Boots the full Spring context against a real Oracle DB - requires a live
// database (see docker/local setup docs). Tagged so CI can skip it while
// everything else still runs; local `./gradlew build` still runs it by default.
@Tag("requires-db")
@SpringBootTest
class OsdrmsaApplApplicationTests {

	@Test
	void contextLoads() {
	}

}
