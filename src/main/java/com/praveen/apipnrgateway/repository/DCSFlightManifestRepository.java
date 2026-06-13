package com.praveen.apipnrgateway.repository;

import com.praveen.apipnrgateway.dto.AuthorityDirection;
import com.praveen.apipnrgateway.entity.DcsFlightManifest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DCSFlightManifestRepository extends JpaRepository<DcsFlightManifest,Integer> {
    Optional<DcsFlightManifest> findByFlightId(String flightId);

//   List<DcsFlightManifest> findDistinctByFlightIdAndPassengers_AppClearance_GovernmentClearanceResponse_AuthorityDirective(String flightId, AuthorityDirection authorityDirection);
   List<DcsFlightManifest> findDistinctByFlightId(String flightId);
   List<DcsFlightManifest> findByIsManifestClosedFalseAndScheduledDepartureDateTimeBefore(LocalDateTime cutoffTime);

}
