package org.rocs.osdrmsa.service.guardian;

import org.rocs.osdrmsa.domain.person.guardian.Guardian;

import java.util.List;

public interface GuardianService {

    List<Guardian> getByStudentId(String studentId);
}
