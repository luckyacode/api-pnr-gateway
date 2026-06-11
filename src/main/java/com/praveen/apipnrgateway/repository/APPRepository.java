package com.praveen.apipnrgateway.repository;

import com.praveen.apipnrgateway.entity.APP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface APPRepository extends JpaRepository<APP,Integer> {
    Optional<APP> findByAppId(String appId);
    Optional<APP> findByPnrId(String pnrId);
    Optional<APP> findByGovernmentClearanceResponse_ClearanceId(String clearanceId);
    Optional<APP> findByGovernmentClearanceResponse_PassengerId(String passengerId);

}
