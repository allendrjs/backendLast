package org.rocs.osdrmsa.utils.security.enumeration;

import static org.rocs.osdrmsa.utils.security.constant.Authority.*;

public enum Role {

    ROLE_USER(USER_AUTHORITIES),

    ROLE_STAFF(STAFF_AUTHORITIES),

    ROLE_PREFECT(PREFECT_AUTHORITIES),

    ROLE_ADMIN(ADMIN_AUTHORITIES),

    ROLE_PRINCIPAL(PRINCIPAL_AUTHORITIES),

    ROLE_DSS(DSS_AUTHORITIES);

    private final String[] authorities;

    Role(String... authorities) {
        this.authorities = authorities;
    }

    public String[] getAuthorities() {
        return authorities;
    }
}