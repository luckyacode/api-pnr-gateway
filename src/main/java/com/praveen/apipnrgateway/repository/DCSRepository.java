package com.praveen.apipnrgateway.repository;

import com.praveen.apipnrgateway.entity.DCS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DCSRepository extends JpaRepository<DCS,Integer> {
    Optional<DCS> findByFlightIdAndPassengerId(String flightId, String passengerId);
//    Optional<DCS> findByAppId(String appId);
//    Optional<DCS> findByPnrId(String pnrId);
//    Optional<DCS> findByGovernmentClearanceResponse_ClearanceId(String clearanceId);
//    Optional<DCS> findByGovernmentClearanceResponse_PassengerId(String passengerId);

}
