package org.rocs.osdrmsa.utils.security.constant;

public final class Authority {

    private Authority() {
    }
    public static final String[] USER_AUTHORITIES = {
            "record:read"
    };

    public static final String[] STAFF_AUTHORITIES = {
            "record:read",
            "record:update",
            "appeal:read"
    };

    public static final String[] PREFECT_AUTHORITIES = {
            "record:read",
            "record:create",
            "record:update",
            "appeal:read",
            "appeal:update"
    };

    public static final String[] ADMIN_AUTHORITIES = {
            "user:manage",
            "record:read",
            "record:create",
            "record:update",
            "record:delete",
            "appeal:read",
            "appeal:update"
    };

    // Read-only oversight roles — case-report access only, no create/update/delete.
    public static final String[] PRINCIPAL_AUTHORITIES = {
            "record:read"
    };

    public static final String[] DSS_AUTHORITIES = {
            "record:read"
    };
}
