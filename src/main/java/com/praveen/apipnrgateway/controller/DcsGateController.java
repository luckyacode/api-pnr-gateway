package com.praveen.apipnrgateway.controller;

import com.praveen.apipnrgateway.dto.ApiResponse;
import com.praveen.apipnrgateway.dto.AuthorityDirection;
import com.praveen.apipnrgateway.dto.BoardingScanRequest;
import com.praveen.apipnrgateway.dto.GovernmentClearanceResponse;
import com.praveen.apipnrgateway.entity.DcsFlightManifest;
import com.praveen.apipnrgateway.service.DcsGateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dcs-request-event.avsc/gates") // Clean plural resource
@RequiredArgsConstructor
@Slf4j
public class DcsGateController {

    private final DcsGateService dcsGateService;

    /**
     * Fetch flight manifest metadata.
     * Path: GET /api/v1/dcs-request-event.avsc/gates/flight-manifest/SQ22
     */
    @GetMapping("/flight-manifest/{flightId}")
    public ResponseEntity<ApiResponse<DcsFlightManifest>> searchFlightManifest(@PathVariable String flightId) {
        return dcsGateService.getFlightManifest(flightId)
                .map(manifest -> ApiResponse.ok(manifest, "Flight Manifest Retreived"))
                .orElseGet(() -> ApiResponse.notFound("Flight manifest not found for ID: " + flightId));
    }

    /**
     * Fetch all passengers with a specific regulatory/authority block.
     * Path: GET /api/v1/dcs-request-event.avsc/gates/flight-manifest/SQ22/blocked-passengers?direction=DENY_BOARDING
     */
    @GetMapping("/flight-manifest/{flightId}/blocked-passengers")
    public ResponseEntity<ApiResponse<List<GovernmentClearanceResponse>>> fetchAllBlockedPassengerInFlight(
            @PathVariable String flightId,
            @RequestParam("direction") AuthorityDirection authorityDirection) {

        List<GovernmentClearanceResponse> blockedList = dcsGateService.getBlockedPassengers(flightId, authorityDirection);
        return ApiResponse.ok(blockedList, "Fetched blocked passengers successfully");
    }

    /**
     * Scan a boarding pass and clear the gate lock.
     * Path: POST /api/v1/dcs-request-event.avsc/gates/scan-boarding-pass
     */
    @PostMapping("/scan-boarding-pass")
    public ResponseEntity<ApiResponse<String>> scanAndBoard(@RequestBody BoardingScanRequest request) {
        log.info("Gate boarding request initiated for Flight: {} and Passenger: {}", request.getFlightId(), request.getPassengerId());

        // Let the service handle business exceptions; controller dictates response structure
        try {
            String gateMessage = dcsGateService.processBoarding(request.getFlightId(), request.getPassengerId());
            return ApiResponse.ok(gateMessage, "Boarding Cleared");
        } catch (Exception ex) {
            log.error("🚨 GATE ALARM INTERACTED: Passenger ID {} triggered security hold!", request.getPassengerId());
            return ApiResponse.error(ex.getMessage()); // Returns your Constants.FAILURE structure
        }
    }
}