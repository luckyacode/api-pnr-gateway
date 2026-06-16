package com.praveen.apipnrgateway.kafka;


import com.praveen.apipnrgateway.helper.CommonMapper;
import com.praveen.apipnrgateway.helper.Utils;
import com.praveen.apipnrgateway.dto.CheckInRequest;
import com.praveen.apipnrgateway.dto.DCSRequest;
import com.praveen.apipnrgateway.dto.GovernmentClearanceResponse;
import com.praveen.apipnrgateway.dto.PnrRequest;
import com.praveen.apipnrgateway.entity.CheckInResponse;
import com.praveen.apipnrgateway.entity.PNR;
import com.praveen.apipnrgateway.kafka.events.*;
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
    private final CommonMapper commonMapper;

    public void processPnrMessage(PnrEvent pnrEvent){
        log.info("Processing PNR ...{}",pnrEvent.pnrId());
        PnrRequest pnrRequest = commonMapper.toPnrRequest(pnrEvent);
        pnrService.addPNR(pnrRequest);
    }

    @SneakyThrows
    public void processCheckInMessage(CheckInEvent checkInEvent) {
        log.info("Processing CheckIn ...{}",checkInEvent);
        PNR pnr = pnrService.getPnrById(checkInEvent.pnrId());
        CheckInRequest checkInRequest = commonMapper.toCheckInRequest(checkInEvent);
        GovernmentClearanceResponse  governmentClearanceResponse = governmentSimulatorService.processClearance(checkInRequest,pnr);
        CheckInResponseEvent checkInResponseEvent = CheckInResponseEvent.builder().
                pnrId(checkInEvent.pnrId()).governmentClearanceResponse(governmentClearanceResponse).build();

        String checkInResponseEventJson = Utils.objectToJson(checkInResponseEvent);
        kafkaPublisher.sendKafkaEvent(KafkaTopics.CheckIn.RESPONSES,checkInEvent.pnrId(),checkInResponseEventJson);
    }

    public void processDCSMessage(DCSRequestEvent dcsRequestEvent) {
        log.info("Processing DCS Message : {}",dcsRequestEvent);
        dcsOperationsService.executeAirportCheckIn(dcsRequestEvent.getFlightId(), dcsRequestEvent.getPnrId(), dcsRequestEvent.getPassengerId(),dcsRequestEvent.getPassengerName());
        log.info("DCS Completed ... ");
    }
}
