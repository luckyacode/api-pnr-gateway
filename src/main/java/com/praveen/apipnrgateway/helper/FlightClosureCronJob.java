package com.praveen.apipnrgateway.helper;

import com.praveen.apipnrgateway.entity.DcsFlightManifest;
import com.praveen.apipnrgateway.repository.DCSFlightManifestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FlightClosureCronJob {
    private final DCSFlightManifestRepository dcsFlightManifestRepository;

    @Scheduled(cron = "0 */5 * * * *") // Runs every 5 minutes
    @Transactional
    public void autoCloseDepartedFlights() {
        LocalDateTime currentTime = LocalDateTime.now();
        log.info("Looking Flight that will departure soon : {}",currentTime);

        LocalDateTime cutoffTime = currentTime.minusMinutes(5);

        List<DcsFlightManifest> activeManifests = dcsFlightManifestRepository
                .findByIsManifestClosedFalseAndScheduledDepartureDateTimeBefore(cutoffTime);
        if(activeManifests.isEmpty()) {
            log.info("No Flight are ready now : {}", currentTime);
        } else {
            log.info("Flight is ready to departure now : {}",currentTime);
        }
        for (DcsFlightManifest manifest : activeManifests) {
            log.info("Flight is departuring now  {} from airport {}",manifest.getFlightId(),manifest.getDeparturePort());
            manifest.setManifestClosed(Boolean.TRUE);
            manifest.setFinalManifestClosedAt(currentTime);

            dcsFlightManifestRepository.save(manifest);
            log.info("DCS BATCH: Auto-closed manifest for flight: {}", manifest.getFlightId());
        }
    }
}