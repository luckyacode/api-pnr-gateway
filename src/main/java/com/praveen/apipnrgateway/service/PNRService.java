package com.praveen.apipnrgateway.service;

import com.praveen.apipnrgateway.helper.AirlineException;
import com.praveen.apipnrgateway.helper.CommonMapper;
import com.praveen.apipnrgateway.helper.Utils;
import com.praveen.apipnrgateway.dto.PNRRequest;
import com.praveen.apipnrgateway.entity.FlightManifest;
import com.praveen.apipnrgateway.entity.PNR;
import com.praveen.apipnrgateway.entity.Passenger;
import com.praveen.apipnrgateway.repository.FlightRepository;
import com.praveen.apipnrgateway.repository.PNRRepository;
import com.praveen.apipnrgateway.repository.PassengerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PNRService {
    private final PNRRepository pnrRepository;
    private final CommonMapper commonMapper;
    private final PassengerRepository passengerRepository;
    private final FlightRepository flightRepository;

    public PNR getPNRById(String id) {
        return getOptionalPNRById(id).orElseThrow(() -> AirlineException.notFound("PNR with " + id + " not found"));
    }

    public Optional<PNR> getOptionalPNRById(String id)  {
        return pnrRepository.findBypnrId(id);
    }

    public List<PNR> findAll(){
        return pnrRepository.findAll();
    }

    public PNR addPNR(PNRRequest pnrRequest) {
        PNR mappedPNR = commonMapper.toPNR(pnrRequest);
        FlightManifest flightManifest = flightRepository.findByFlightId(pnrRequest.getFlightId()).orElseThrow(() -> AirlineException.serverError("flight not scheduled..."));
        mappedPNR.setFlight(flightManifest);
        Passenger passenger = passengerRepository.findById(pnrRequest.getPassengerId()).orElseThrow(() -> AirlineException.serverError("passenger not found..."));
        mappedPNR.setPassenger(passenger);
        PNR pnr = pnrRepository.save(mappedPNR);
        log.info("Saved PNR to db . {}", pnr);
        return pnr;
    }

    public Optional<String> getEdifactMessageByPnrId(String id) {
        return getOptionalPNRById(id).map(Utils::convertToEdifact);
    }
}
