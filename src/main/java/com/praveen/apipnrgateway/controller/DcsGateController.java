package com.praveen.apipnrgateway.controller;

import com.praveen.apipnrgateway.dto.AuthorityDirection;
import com.praveen.apipnrgateway.dto.DcsStatus;
import com.praveen.apipnrgateway.dto.GovernmentClearanceResponse;
import com.praveen.apipnrgateway.entity.APP;
import com.praveen.apipnrgateway.entity.DcsFlightManifest;
import com.praveen.apipnrgateway.entity.DcsPassengerManifest;
import com.praveen.apipnrgateway.helper.AirlineException;
import com.praveen.apipnrgateway.repository.DCSFlightManifestRepository;
import com.praveen.apipnrgateway.repository.DCSPassengerManifestRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/v1/dcs/gate")
@RequiredArgsConstructor
@Slf4j
public class DcsGateController {

    private final DCSPassengerManifestRepository dcsPassengerManifestRepository;
    private final DCSFlightManifestRepository dcsFlightManifestRepository;

    @SneakyThrows
    @GetMapping("/flightManifest/{flightId}")
    public DcsFlightManifest searchFlightManifest(String flightId){
        return dcsFlightManifestRepository.findByFlightId(flightId).orElseThrow(()-> AirlineException.badRequest("flight now found"));
    }

    @SneakyThrows
    @GetMapping("/fetchAllBlockedPassengerInFlight/{flightId}")
    public List<GovernmentClearanceResponse> fetchAllBlockedPassengerInFlight(String flightId, AuthorityDirection authorityDirection){
        return dcsFlightManifestRepository.findDistinctByFlightId(flightId).stream().map(DcsFlightManifest::getPassengers).flatMap(Collection::stream).map(DcsPassengerManifest::getAppClearance).
                map(APP::getGovernmentClearanceResponse).filter(m->m.getAuthorityDirective()==authorityDirection).toList();
    }

    @PostMapping("/scan-boarding-pass")
    public ResponseEntity<String> scanAndBoard(@RequestParam String flightId, @RequestParam String passengerId) {
        
        log.info("scan and board searching for the flight id {} and {}",flightId,passengerId);
        DcsPassengerManifest passenger = dcsPassengerManifestRepository.findByPassengerId(passengerId)
                .orElseThrow(() -> new EntityNotFoundException("Passenger not found on this flight manifest."));

        // DCS Enforcement Check 1: Is the passenger locked due to a government DNL?
        if (passenger.getDcsStatus() == DcsStatus.BOARDING_LOCKED) {
            log.error("🚨 GATE ALARM: Attempts to board a BLOCKED passenger (ID: {})!", passengerId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("❌ BOARDING DENIED: Government security hold active. Do not allow passenger past the gate.");
        }

        // DCS Enforcement Check 2: Did they skip check-in?
        if (passenger.getDcsStatus() == DcsStatus.NOT_CHECKED_IN) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("❌ REJECTED: Passenger has not checked in or assigned a seat.");
        }

        // Success: Passenger boards the aircraft
        passenger.setDcsStatus(DcsStatus.BOARDED);
        passenger.setLastUpdatedTime(LocalDateTime.now());
        dcsPassengerManifestRepository.save(passenger);

        log.info("✈️ Passenger {} successfully boarded flight {}", passengerId, flightId);
        return ResponseEntity.ok("✅ WELCOME ABOARD: Boarding cleared. Gate lock released.");
    }
}