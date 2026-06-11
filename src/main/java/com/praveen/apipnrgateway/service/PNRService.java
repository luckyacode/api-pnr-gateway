package com.praveen.apipnrgateway.service;

import com.praveen.apipnrgateway.CommonMapper;
import com.praveen.apipnrgateway.Utils;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class PNRService {
    private final PNRRepository pnrRepository;
    private final CommonMapper commonMapper;
    private final PassengerRepository passengerRepository;
    private final FlightRepository flightRepository;

    public PNR getPNRById(String id) throws Exception {
        return pnrRepository.findBypnrId(id).orElseThrow(()->new Exception("pnr not found..."));
    }

    public List<PNR> findAll(){
        return pnrRepository.findAll();
    }

    public PNR addPNR(PNRRequest pnrRequest) {
        PNR mappedPNR = commonMapper.toPNR(pnrRequest);
        FlightManifest flightManifest = flightRepository.findByFlightId(pnrRequest.getFlightId()).orElseThrow(() -> new RuntimeException("flight not scheduled..."));
        mappedPNR.setFlight(flightManifest);
        Passenger passenger = passengerRepository.findById(pnrRequest.getPassengerId()).orElseThrow(() -> new RuntimeException("passenger not found..."));
        mappedPNR.setPassenger(passenger);
        PNR pnr = pnrRepository.save(mappedPNR);
        log.info("Saved PNR to db . {}", pnr);
        return pnr;
    }

    public String getPNRMessageById(String id) throws Exception {
        return Utils.convertToEdifact(getPNRById(id));
    }
}
