package org.rocs.osdrmsa.domain.login;

public enum Role {
    ROLE_ADMIN,
    ROLE_PREFECT,
    ROLE_STAFF,
    ROLE_USER,
    // Read-only oversight roles (school Principal / Discipline & Student
    // Services officer) — see RC-OSD system plan req. 5. Case-report access
    // only; these roles cannot decide requests/appeals or edit records.
    ROLE_PRINCIPAL,
    ROLE_DSS
}