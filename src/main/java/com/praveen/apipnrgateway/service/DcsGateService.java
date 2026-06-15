package com.praveen.apipnrgateway.service;

import com.praveen.apipnrgateway.dto.AuthorityDirection;
import com.praveen.apipnrgateway.dto.DcsStatus;
import com.praveen.apipnrgateway.dto.GovernmentClearanceResponse;
import com.praveen.apipnrgateway.entity.APP;
import com.praveen.apipnrgateway.entity.DcsFlightManifest;
import com.praveen.apipnrgateway.entity.DcsPassengerManifest;
import com.praveen.apipnrgateway.helper.AirlineException;
import com.praveen.apipnrgateway.repository.DCSFlightManifestRepository;
import com.praveen.apipnrgateway.repository.DCSPassengerManifestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DcsGateService {

    private final DCSPassengerManifestRepository dcsPassengerManifestRepository;
    private final DCSFlightManifestRepository dcsFlightManifestRepository;

    public Optional<DcsFlightManifest> getFlightManifest(String flightId) {
        return dcsFlightManifestRepository.findByFlightId(flightId);
    }

    @Transactional(readOnly = true)
    public List<GovernmentClearanceResponse> getBlockedPassengers(String flightId, AuthorityDirection authorityDirection) {
        return dcsFlightManifestRepository.findDistinctByFlightId(flightId).stream()
                .map(DcsFlightManifest::getPassengers)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .map(DcsPassengerManifest::getAppClearance)
                .filter(Objects::nonNull) // FIX: Safeguard against NullPointer if AppClearance is missing
                .map(APP::getGovernmentClearanceResponse)
                .filter(Objects::nonNull)
                .filter(clearance -> clearance.getAuthorityDirective() == authorityDirection)
                .toList();
    }

    @Transactional // Critical for write operations in production
    public String processBoarding(String flightId, String passengerId) {
        DcsPassengerManifest passenger = dcsPassengerManifestRepository.findByPassengerId(passengerId)
                .orElseThrow(() -> new IllegalArgumentException("Passenger not found on this flight manifest."));

        // DCS Enforcement Check 1: Is the passenger locked due to a government Do Not Fly (DNL)?
        if (passenger.getDcsStatus() == DcsStatus.BOARDING_LOCKED) {
            throw AirlineException.forbidden("❌ BOARDING DENIED: Government security hold active. Do not allow passenger past the gate.");
        }

        // DCS Enforcement Check 2: Did they skip check-in?
        if (passenger.getDcsStatus() == DcsStatus.NOT_CHECKED_IN) {
            throw AirlineException.unAuthorize("❌ REJECTED: Passenger has not checked in or been assigned a seat.");
        }

        // Success: Passenger boards the aircraft
        passenger.setDcsStatus(DcsStatus.BOARDED);
        passenger.setLastUpdatedTime(LocalDateTime.now());
        dcsPassengerManifestRepository.save(passenger);

        log.info("✈️ Passenger {} successfully boarded flight {}", passengerId, flightId);
        return "✅ WELCOME ABOARD: Boarding cleared. Gate lock released.";
    }
}