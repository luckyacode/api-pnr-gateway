package com.praveen.apipnrgateway;

import com.praveen.apipnrgateway.dto.DcsStatus;
import com.praveen.apipnrgateway.entity.DCS;
import com.praveen.apipnrgateway.repository.DCSRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/dcs/gate")
@RequiredArgsConstructor
@Slf4j
public class DcsGateController {

    private final DCSRepository dcsRepository;

    @PostMapping("/scan-boarding-pass")
    public ResponseEntity<String> scanAndBoard(@RequestParam String flightId, @RequestParam String passengerId) {
        
        DCS passenger = dcsRepository.findByFlightIdAndPassengerId(flightId, passengerId)
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
        passenger.setUpdatedByDcsAt(LocalDateTime.now());
        dcsRepository.save(passenger);

        log.info("✈️ Passenger {} successfully boarded flight {}", passengerId, flightId);
        return ResponseEntity.ok("✅ WELCOME ABOARD: Boarding cleared. Gate lock released.");
    }
}