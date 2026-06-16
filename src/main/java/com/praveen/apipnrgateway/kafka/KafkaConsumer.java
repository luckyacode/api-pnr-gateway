package com.praveen.apipnrgateway.kafka;

import com.praveen.apipnrgateway.helper.Utils;
import com.praveen.apipnrgateway.dto.CheckInRequest;
import com.praveen.apipnrgateway.dto.DCSRequest;
import com.praveen.apipnrgateway.dto.PnrRequest;
import com.praveen.apipnrgateway.kafka.events.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {

    private final KafkaService kafkaService;

    @KafkaListener(topics = KafkaTopics.PNR_EVENTS, groupId = "group-id1")
    public void consumingPnrRequest(@Payload String request, @Header(value = KafkaHeaders.RECEIVED_KEY) String pnrId) {
        log.info("PnrRequest Message received with key {} and message : {}", pnrId, request);
        PnrRequest pnrRequest = Utils.jsonToObject(request, PnrRequest.class);
        log.info("Successfully Message Received : {}", pnrRequest);
        kafkaService.processPNRMessage(pnrRequest);
    }

    @KafkaListener(topics = KafkaTopics.CheckIn.REQUESTS, groupId = "group-id2")
    public void consumingCheckInRequest(@Payload String request, @Header(value = KafkaHeaders.RECEIVED_KEY) String pnrId) {
        log.info("CheckInRequest Message received with key {} and message : {}", pnrId, request);
        CheckInRequest checkInRequest = Utils.jsonToObject(request, CheckInRequest.class);
        log.info("Successfully Message Received : {}", checkInRequest);
        kafkaService.processCheckInMessage(checkInRequest);
    }

    @KafkaListener(topics = KafkaTopics.DCS_EVENTS, groupId = "group-id3")
    public void consumingDCSRequest(@Payload String request, @Header(value = KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("DCSRequest Message received with key {} and message : {}", key, request);
        DCSRequest dcsRequest = Utils.jsonToObject(request, DCSRequest.class);
        log.info("Successfully Message Received : {}", dcsRequest);
        kafkaService.processDCSMessage(dcsRequest);
    }


}
