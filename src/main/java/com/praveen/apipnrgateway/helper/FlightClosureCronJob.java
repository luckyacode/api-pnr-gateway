package com.praveen.apipnrgateway.helper;

import com.praveen.apipnrgateway.dto.DcsStatus;
import com.praveen.apipnrgateway.entity.DcsFlightManifest;
import com.praveen.apipnrgateway.entity.DcsPassengerManifest;
import com.praveen.apipnrgateway.repository.DCSFlightManifestRepository;
import com.praveen.apipnrgateway.repository.DCSPassengerManifestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FlightClosureCronJob {
    private final DCSFlightManifestRepository dcsFlightManifestRepository;
    private final DCSPassengerManifestRepository dcsPassengerManifestRepository;
    @Scheduled(cron = "0 */1 * * * *") // Runs every 5 minutes
    @Transactional
    public void autoCloseDepartedFlights() {
        Instant currentTime = Instant.now();
        log.info("Looking Flight that will departure soon : {}",currentTime);

        Instant cutoffTime = currentTime.minus(Duration.ofMinutes(5));

        List<DcsFlightManifest> activeManifests = dcsFlightManifestRepository
                .findByIsManifestClosedFalseAndScheduledDepartureDateTimeBefore(cutoffTime);
        if(activeManifests.isEmpty()) {
            log.info("No Flight are ready now : {}", currentTime);
        } else {
            log.info("Flight is ready to departure now : {}",currentTime);
        }
        for (DcsFlightManifest manifest : activeManifests) {
            log.info("Flight is departuring now  {} from airport {}",manifest.getFlightId(),manifest.getDeparturePort());
            log.info("Fetching all boarded passenger : ");
            List<DcsPassengerManifest> plist = dcsPassengerManifestRepository.findByDcsManifest_flightIdAndDcsStatus(manifest.getFlightId(), DcsStatus.CHECKED_IN);
            plist.forEach(passenger -> {
                log.info("Passenger onboarded in flight {} -> : [ {} {} ]",manifest.getFlightId(), passenger.getPassengerId(),passenger.getPassengerName());
            });
            manifest.setManifestClosed(Boolean.TRUE);
            manifest.setFinalManifestClosedAt(currentTime);

            dcsFlightManifestRepository.save(manifest);
            log.info("DCS BATCH: Auto-closed manifest for flight: {}", manifest.getFlightId());
        }
    }
}