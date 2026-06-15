package com.praveen.apipnrgateway.service;

import com.praveen.apipnrgateway.helper.Utils;
import com.praveen.apipnrgateway.dto.AuthorityDirection;
import com.praveen.apipnrgateway.dto.DcsStatus;
import com.praveen.apipnrgateway.entity.APP;
import com.praveen.apipnrgateway.entity.DcsFlightManifest;
import com.praveen.apipnrgateway.entity.DcsPassengerManifest;
import com.praveen.apipnrgateway.entity.FlightManifest;
import com.praveen.apipnrgateway.helper.AirlineException;
import com.praveen.apipnrgateway.repository.APPRepository;
import com.praveen.apipnrgateway.repository.DCSFlightManifestRepository;
import com.praveen.apipnrgateway.repository.DCSPassengerManifestRepository;
import com.praveen.apipnrgateway.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class DcsOperationsService {

    private final APPRepository appRepository;
    private final DCSPassengerManifestRepository dcsPassengerManifestRepository;
    private final DCSFlightManifestRepository dcsFlightManifestRepository;
    private final FlightRepository flightRepository;

    @SneakyThrows
    @Transactional
    public DcsPassengerManifest executeAirportCheckIn(String flightId, String pnrId, String passengerId, String passengerName)  {
        log.info("DCS: Initiating airport desk check-in for Pax: {} on Flight: {}", passengerId, flightId);

        // 1. Fetch core static routing configuration data
        FlightManifest flight = flightRepository.findByFlightId(flightId)
                .orElseThrow(() -> AirlineException.badRequest("Flight  not found"));

        // 2. Fetch or build the parent Flight Manifest entity
        DcsFlightManifest dcsFlightManifest = dcsFlightManifestRepository.findByFlightId(flightId)
                .orElseGet(() -> DcsFlightManifest.builder()
                        .flightId(flightId)
                        .departurePort(flight.getDepartureAirport())
                        .arrivalPort(flight.getArrivalAirport())
                        .flightDate(flight.getDepartureDate().toString())
                        .scheduledDepartureDateTime(flight.getScheduledDepartureDateTime())
                        .scheduledArrivalDateTime(flight.getScheduledArrivalDateTime())
//                        .totalCheckedBags(1)
//                        .totalBaggageWeightKg(1.0)
                        .manifestHydratedAt(LocalDateTime.now())
                        .isManifestClosed(Boolean.FALSE)
                        .build());

        if(dcsFlightManifest.isManifestClosed()){
            log.info("Sorry, Flight is already departured .....no new passenger allowed...");
            throw AirlineException.serverError("Flight is already departured .");
        }

        Random r = new Random();
        log.info("DCS: Fetching or creating passenger line-item manifest row securely.");

        // 🌟 FIXED: Passed passengerId first, flightId second to match your repository signature perfectly!
        DcsPassengerManifest dcsPassengerManifest = dcsPassengerManifestRepository
                .findByPassengerId(passengerId)
                .orElseGet(() -> DcsPassengerManifest.builder()
                        .pnrId(pnrId)
                        .passengerId(passengerId)
                        .passengerName(passengerName)
                        .baggageCount(Utils.next(1, 4))
                        .totalBagWeight(Utils.nextDouble(1, 7))
                        .specialServiceRequest("WCHR/VGML")
                        .dcsStatus(DcsStatus.NOT_CHECKED_IN)
                        .lastUpdatedTime(LocalDateTime.now())
                        .build());

        // 🌟 FIXED: Bidirectional binding tool strategy.
        // Ensure the helper method flightManifest.addPassenger(passenger) is called,
        // or set both sides explicitly right here before writing:
        dcsFlightManifest.setTotalCheckedBags(dcsFlightManifest.getTotalCheckedBags()+dcsPassengerManifest.getBaggageCount());
        dcsFlightManifest.setTotalBaggageWeightKg(dcsFlightManifest.getTotalBaggageWeightKg()+dcsPassengerManifest.getTotalBagWeight());
        dcsPassengerManifest.setDcsManifest(dcsFlightManifest);
        if (!dcsFlightManifest.getPassengers().contains(dcsPassengerManifest)) {
            dcsFlightManifest.getPassengers().add(dcsPassengerManifest);
        }
        try{
        // 3. Process Government APP Clearances from Database Snapshot Audit
        APP appClearanceResult = appRepository.findByGovernmentClearanceResponse_PassengerId(passengerId)
                .orElseThrow(() -> AirlineException.serverError("Regulatory clearance data missing for passenger identity token"));

        dcsPassengerManifest.setAppClearance(appClearanceResult);
        AuthorityDirection directive = appClearanceResult.getGovernmentClearanceResponse().getAuthorityDirective();

        // 4. Enforce Border Directives
        if (AuthorityDirection.DNL == directive) {
            log.info("DCS complain : {}",directive.getDescription());
            log.error("DCS COMPLIANCE ALERT: Government returned DNL. Hard locking passenger  {}",passengerId);
            dcsPassengerManifest.setDcsStatus(DcsStatus.BOARDING_LOCKED);

            // Save the parent aggregate root container (cascades down and saves the locked passenger row)
            dcsPassengerManifestRepository.save(dcsPassengerManifest);
            dcsFlightManifestRepository.save(dcsFlightManifest);

            throw AirlineException.serverError("REGULATORY LOCK: Boarding pass generation blocked by government authority.");
        }

            if (AuthorityDirection.CHCK==directive) {
                log.warn("DCS: Manual documentation check required at gate.");
                // Still allow check-in, but flag it for manual review later
            }

            // 4. Success Path: Government returned "OK", DCS updates state and assigns a seat
            dcsPassengerManifest.setDcsStatus(DcsStatus.CHECKED_IN);
            dcsPassengerManifest.setSeatNumber(assignRandomSeat());
            dcsPassengerManifest.setLastUpdatedTime(LocalDateTime.now());

            log.info("DCS: Check-in successful. Seat {} assigned to Pax: {}", dcsPassengerManifest.getSeatNumber(), passengerId);
            dcsFlightManifestRepository.save(dcsFlightManifest);
            return dcsPassengerManifestRepository.save(dcsPassengerManifest);

        } catch (SecurityException e) {
            throw e; // Bubble up security denials cleanly to the UI layer
        } catch (Exception e) {
            log.error("DCS: Internal fallback processing triggered due to system error: ", e);
            dcsPassengerManifest.setDcsStatus(DcsStatus.BOARDING_LOCKED); // Safe fallback: lock if system fails
            dcsFlightManifestRepository.save(dcsFlightManifest);
            return dcsPassengerManifestRepository.save(dcsPassengerManifest);
        }
    }

    private String assignRandomSeat() {
        return "12" + (char) ('A' + new java.util.Random().nextInt(6)); // Generates 12A, 12B, etc.
    }
}