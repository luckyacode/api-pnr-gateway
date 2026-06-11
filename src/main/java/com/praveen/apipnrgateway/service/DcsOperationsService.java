package com.praveen.apipnrgateway.service;

import com.praveen.apipnrgateway.dto.AuthorityDirection;
import com.praveen.apipnrgateway.dto.CheckInRequest;
import com.praveen.apipnrgateway.dto.DcsStatus;
import com.praveen.apipnrgateway.entity.APP;
import com.praveen.apipnrgateway.entity.DCS;
import com.praveen.apipnrgateway.repository.APPRepository;
import com.praveen.apipnrgateway.repository.DCSRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class DcsOperationsService {

    private final DCSRepository dcsRepository;
    private final APPRepository appRepository;

    @Transactional
    public DCS executeAirportCheckIn(String flightId, String pnrId, String passengerId) {
        log.info("DCS: Initiating airport desk check-in for Pax: {} on Flight: {}", passengerId, flightId);

        DCS manifestRecord = dcsRepository.findByFlightIdAndPassengerId(flightId, passengerId)
                .orElseGet(() -> DCS.builder()
                        .flightId(flightId)
                        .pnrId(pnrId)
                        .passengerId(passengerId)
                        .dcsStatus(DcsStatus.NOT_CHECKED_IN)
                        .build());

        try {
            // 2. DCS triggers the real-time APP message handshake over Kafka to the Gov Simulator
//            APP appClearanceResult = appProcessor.triggerGovernmentVetting(checkInRequest);
            APP appClearanceResult = appRepository.findByPnrId(pnrId).orElseThrow(()->new RuntimeException("pnr not found"));

            manifestRecord.setAppClearance(appClearanceResult);
            AuthorityDirection directive = appClearanceResult.getGovernmentClearanceResponse().getAuthorityDirective();

            // 3. DCS evaluates the directive rule to update the physical manifest status
            if (AuthorityDirection.DNL==directive) {
                log.error("DCS COMPLIANCE ALERT: Government returned DNL. Hard locking passenger row.");
                manifestRecord.setDcsStatus(DcsStatus.BOARDING_LOCKED);
                dcsRepository.save(manifestRecord);
                
                throw new Exception("REGULATORY LOCK: Boarding pass generation blocked by government authority.");
            } 
            
            if (AuthorityDirection.CHCK==directive) {
                log.warn("DCS: Manual documentation check required at gate.");
                // Still allow check-in, but flag it for manual review later
            }

            // 4. Success Path: Government returned "OK", DCS updates state and assigns a seat
            manifestRecord.setDcsStatus(DcsStatus.CHECKED_IN);
            manifestRecord.setSeatNumber(assignRandomSeat());
            manifestRecord.setUpdatedByDcsAt(LocalDateTime.now());
            
            log.info("DCS: Check-in successful. Seat {} assigned to Pax: {}", manifestRecord.getSeatNumber(), passengerId);
            return dcsRepository.save(manifestRecord);

        } catch (SecurityException e) {
            throw e; // Bubble up security denials cleanly to the UI layer
        } catch (Exception e) {
            log.error("DCS: Internal fallback processing triggered due to system error: ", e);
            manifestRecord.setDcsStatus(DcsStatus.BOARDING_LOCKED); // Safe fallback: lock if system fails
            return dcsRepository.save(manifestRecord);
        }
    }

    private String assignRandomSeat() {
        return "12" + (char) ('A' + new java.util.Random().nextInt(6)); // Generates 12A, 12B, etc.
    }
}