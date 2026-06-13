package com.praveen.apipnrgateway;

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
        log.info("Calling auto closure flight...");
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(5);

        // Find flights that have departed but whose manifests are still open
        List<DcsFlightManifest> activeManifests = dcsFlightManifestRepository
                .findByIsManifestClosedFalseAndScheduledDepartureDateTimeBefore(cutoffTime);
        if(activeManifests.isEmpty())
            log.info("no active flight found to close auto....");
        else {
            log.info("auto closer flight founds ");
        }
        for (DcsFlightManifest manifest : activeManifests) {
            log.info("flight is auto closing : {}",manifest.getFlightId());
            manifest.setManifestClosed(Boolean.TRUE);
            manifest.setFinalManifestClosedAt(LocalDateTime.now());

            dcsFlightManifestRepository.save(manifest);
            log.info("DCS BATCH: Auto-closed manifest for flight: {}", manifest.getFlightId());
        }
    }
}