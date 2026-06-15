package com.praveen.apipnrgateway.kafka;


import com.praveen.apipnrgateway.helper.Utils;
import com.praveen.apipnrgateway.dto.CheckInRequest;
import com.praveen.apipnrgateway.dto.DCSRequest;
import com.praveen.apipnrgateway.dto.GovernmentClearanceResponse;
import com.praveen.apipnrgateway.dto.PNRRequest;
import com.praveen.apipnrgateway.entity.CheckInResponse;
import com.praveen.apipnrgateway.entity.PNR;
import com.praveen.apipnrgateway.service.DcsOperationsService;
import com.praveen.apipnrgateway.service.GovernmentSimulatorService;
import com.praveen.apipnrgateway.service.PnrService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaService {
    private final KafkaTemplate<String,String>  kafkaTemplate;
    private final PnrService pnrService;
    private final GovernmentSimulatorService governmentSimulatorService;
    private final KafkaPublisher kafkaPublisher;
    private final DcsOperationsService dcsOperationsService;
    public void processPNRMessage(PNRRequest pnrRequest){
        log.info("Processing PNR ...{}",pnrRequest.getPNRId());
        pnrService.addPNR(pnrRequest);
    }

    @SneakyThrows
    public void processCheckInMessage(CheckInRequest checkInRequest) {
        log.info("Processing CheckIn ...{}",checkInRequest);
        PNR pnr = pnrService.getPnrById(checkInRequest.getPnrId());
        GovernmentClearanceResponse  governmentClearanceResponse = governmentSimulatorService.processClearance(checkInRequest,pnr);
        CheckInResponse checkInResponse = CheckInResponse.builder().
                pnrId(checkInRequest.getPnrId()).
                governmentClearanceResponse(governmentClearanceResponse).build();
        String checkInResponseJson = Utils.objectToJson(checkInResponse);
        kafkaPublisher.sendCheckInResponseMessage(governmentClearanceResponse.getClearanceId(),checkInResponseJson);
    }

    public void processDCSMessage(DCSRequest dcsRequest) {
        log.info("Processing DCS Message : {}",dcsRequest);
        dcsOperationsService.executeAirportCheckIn(dcsRequest.getFlightId(), dcsRequest.getPnrId(), dcsRequest.getPassengerId(),dcsRequest.getPassengerName());
        log.info("DCS Completed ... ");
    }
}
