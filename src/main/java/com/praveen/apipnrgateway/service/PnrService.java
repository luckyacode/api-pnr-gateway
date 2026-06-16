package com.praveen.apipnrgateway.service;

import com.praveen.apipnrgateway.helper.AirlineException;
import com.praveen.apipnrgateway.helper.CommonMapper;
import com.praveen.apipnrgateway.helper.Utils;
import com.praveen.apipnrgateway.dto.PnrRequest;
import com.praveen.apipnrgateway.entity.FlightManifest;
import com.praveen.apipnrgateway.entity.PNR;
import com.praveen.apipnrgateway.entity.Passenger;
import com.praveen.apipnrgateway.repository.FlightRepository;
import com.praveen.apipnrgateway.repository.PassengerRepository;
import com.praveen.apipnrgateway.repository.PnrRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PnrService {
    private final PnrRepository pnrRepository;
    private final CommonMapper commonMapper;
    private final PassengerRepository passengerRepository;
    private final FlightRepository flightRepository;

    @Transactional(readOnly = true)
    public PNR getPnrById(String id) {
        log.debug("Searching PNR by record identifier: {}", id);
        return getOptionalPnrById(id)
                .orElseThrow(() -> AirlineException.notFound("PNR record locator '" + id + "' not found in system platform."));
    }

    @Transactional(readOnly = true)
    public Optional<PNR> getOptionalPnrById(String id) {
        return pnrRepository.findByPnrId(id);
    }

    @Transactional(readOnly = true)
    public List<PNR> findAll(){
        return pnrRepository.findAll();
    }


    @Transactional(readOnly = true)
    public List<PNR> findAllByStatus(String status){
        return pnrRepository.findAllByBookingStatus(status);
    }


    @Transactional(readOnly = true)
    public void addPNR(PnrRequest pnrRequest) {
        PNR mappedPNR = commonMapper.toPNR(pnrRequest);
        FlightManifest flightManifest = flightRepository.findByFlightId(pnrRequest.getFlightId()).orElseThrow(() -> AirlineException.serverError("flight not scheduled : "+pnrRequest.getFlightId()));
        mappedPNR.setFlight(flightManifest);
        Passenger passenger = passengerRepository.findById(pnrRequest.getPassengerId()).orElseThrow(() -> AirlineException.notFound("Passenger with id "+pnrRequest.getPassengerId()+"not found"));
        mappedPNR.setPassenger(passenger);
        PNR pnr = pnrRepository.save(mappedPNR);
        log.info("PNR Record saved to db . {}", pnr);
    }

    @Transactional(readOnly = true)
    public Optional<String> getEdifactMessageByPnrId(String id) {
        return getOptionalPnrById(id).map(Utils::convertToEdifact);
    }
}
