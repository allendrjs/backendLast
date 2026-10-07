package org.rocs.osdrmsa.utils.security;

import java.util.Locale;

public final class DefaultCredentials {

    private DefaultCredentials() {
    }

    public static String passwordFor(String lastName) {
        return lastName == null ? "" : lastName.trim().toLowerCase(Locale.ROOT);
    }
}
