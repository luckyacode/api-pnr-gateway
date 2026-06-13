package com.praveen.apipnrgateway.service;


import com.praveen.apipnrgateway.dto.AuthorityDirection;
import com.praveen.apipnrgateway.dto.CheckInRequest;
import com.praveen.apipnrgateway.dto.GovernmentClearanceResponse;
import com.praveen.apipnrgateway.entity.PNR;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class GovernmentSimulatorService {

    public GovernmentClearanceResponse processClearance(CheckInRequest checkInRequest,PNR request) {
        String passengerId = String.valueOf(request.getPassenger().getId());

        // Default Status: Passenger is clear to fly
        String directive = "OK";
        String responseCode = "00";
        String reason = "Clearance Approved - Document Valid";

        // SIMULATED RULE 1: Simulated Watchlist Flag (Triggered by Passenger ID 99)
        if ("99".equals(passengerId)) {
            directive = "DNL"; // Do Not Board
            responseCode = "99";
            reason = "SECURITY ALERT: Passenger is on a non-fly watchlist.";
        }

        // SIMULATED RULE 2: Visa Issue Simulation (Triggered by Passenger ID 50)
        else if ("50".equals(passengerId)) {
            directive = "CHCK"; // Check Manual Documents
            responseCode = "45";
            reason = "VISA VERIFICATION REQUIRED: Destination entry conditions unconfirmed.";
        }

        // SIMULATED RULE 3: Missing Passport info safety catch
        else if (passengerId == null || passengerId.isEmpty() || checkInRequest.getDocumentDetails()==null) {
            directive = "DNL";
            responseCode = "08";
            reason = "REJECTED: Invalid or missing traveler identity details.";
        }

        return GovernmentClearanceResponse.builder()
                .clearanceId(request.getTransactionId() != null ? request.getTransactionId() : UUID.randomUUID().toString())
                .passengerId(passengerId)
                .authorityDirective(AuthorityDirection.valueOf(directive))
                .responseCode(responseCode)
                .denialReason(reason)
                .evaluatedAt(LocalDateTime.now())
                .build();
    }
}