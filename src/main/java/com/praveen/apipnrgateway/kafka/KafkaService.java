package com.praveen.apipnrgateway.kafka;


import com.praveen.apipnrgateway.entity.DlqTopic;
import com.praveen.apipnrgateway.helper.CommonMapper;
import com.praveen.apipnrgateway.helper.Utils;
import com.praveen.apipnrgateway.dto.CheckInRequest;
import com.praveen.apipnrgateway.dto.DCSRequest;
import com.praveen.apipnrgateway.dto.GovernmentClearanceResponse;
import com.praveen.apipnrgateway.dto.PnrRequest;
import com.praveen.apipnrgateway.entity.CheckInResponse;
import com.praveen.apipnrgateway.entity.PNR;
import com.praveen.apipnrgateway.kafka.events.*;
import com.praveen.apipnrgateway.repository.DlqTopicRepository;
import com.praveen.apipnrgateway.service.DcsOperationsService;
import com.praveen.apipnrgateway.service.GovernmentSimulatorService;
import com.praveen.apipnrgateway.service.PnrService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class KafkaService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final PnrService pnrService;
    private final GovernmentSimulatorService governmentSimulatorService;
    private final KafkaPublisher kafkaPublisher;
    private final DcsOperationsService dcsOperationsService;
    private final CommonMapper commonMapper;
    private final DlqTopicRepository dlqTopicRepository;

    public void processPnrMessage(PnrEvent pnrEvent) {
        log.info("Executing local repository append for PNR Locator: {}", pnrEvent.pnrId());
        PnrRequest pnrRequest = commonMapper.toPnrRequest(pnrEvent);
        pnrService.addPNR(pnrRequest);
    }


    public void processCheckInMessage(CheckInEvent checkInEvent) {
        String pnrId = checkInEvent.pnrId();
        log.info("Evaluating background border security clearance verification for PNR: {}", pnrId);

        try {
            PNR pnr = pnrService.getPnrById(pnrId);
            CheckInRequest checkInRequest = commonMapper.toCheckInRequest(checkInEvent);

            // External execution mapping phase
            GovernmentClearanceResponse governmentClearanceResponse = governmentSimulatorService.processClearance(checkInRequest, pnr);

            CheckInResponseEvent checkInResponseEvent = CheckInResponseEvent.builder().pnrId(pnrId).governmentClearanceResponse(governmentClearanceResponse).build();

            log.info("Border clearance validation computed. Publishing results back to cluster topics...");

            kafkaPublisher.sendKafkaEvent(KafkaTopics.CheckIn.RESPONSES, pnrId, Utils.objectToJson(checkInResponseEvent));

        } catch (Exception ex) {
            log.error("🚨 ORCHESTRATION PIPELINE BROKEN: Vetting processing failed for PNR {}. Core reason: {}", pnrId, ex.getMessage());
        }
    }


    public void processDCSMessage(DCSRequestEvent dcsRequestEvent) {
        log.info("Synchronizing Departure Control System processing logs for PNR: {}", dcsRequestEvent.getPnrId());
        dcsOperationsService.executeAirportCheckIn(dcsRequestEvent.getFlightId(), dcsRequestEvent.getPnrId(), dcsRequestEvent.getPassengerId(), dcsRequestEvent.getPassengerName());
        log.info("✓ DCS check-in operations fully recorded for PNR: {}", dcsRequestEvent.getPnrId());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveDlq(DlqTopic dlqTopic) {
        dlqTopicRepository.save(dlqTopic);
        log.info("DLQ Message Record commited in database");
    }
}
