package org.rocs.osdrmsa.domain.department;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Department {

    JHS("Junior High School"),
    SHS("Senior High School"),
    COLLEGE("College");

    private final String displayName;
}
