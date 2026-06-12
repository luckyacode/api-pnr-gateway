package com.praveen.apipnrgateway.repository;

import com.praveen.apipnrgateway.entity.DcsFlightManifest;
import com.praveen.apipnrgateway.entity.DcsPassengerManifest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DCSFlightManifestRepository extends JpaRepository<DcsFlightManifest,Integer> {
    Optional<DcsFlightManifest> findByFlightId(String flightId);

}
