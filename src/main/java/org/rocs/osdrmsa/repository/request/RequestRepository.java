package org.rocs.osdrmsa.repository.request;

import org.rocs.osdrmsa.domain.request.Request;
import org.rocs.osdrmsa.domain.request.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    /** Request.employee is a @ManyToOne Employee, not a flat employeeId string. */
    List<Request> findByEmployee_EmployeeId(String employeeId);

    List<Request> findByStatus(RequestStatus status);
}
