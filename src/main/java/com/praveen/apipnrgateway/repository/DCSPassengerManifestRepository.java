package com.praveen.apipnrgateway.repository;

import com.praveen.apipnrgateway.dto.DcsStatus;
import com.praveen.apipnrgateway.entity.DcsPassengerManifest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DCSPassengerManifestRepository extends JpaRepository<DcsPassengerManifest,Integer> {
//    Optional<DcsPassengerManifest> findByPassengerIdAndDcsManifest_FlightId(String flightId, String passengerId);
    Optional<DcsPassengerManifest> findByPassengerId(String passengerId);
    List<DcsPassengerManifest> findByDcsManifest_flightIdAndDcsStatus(String flightId,DcsStatus status);

}
