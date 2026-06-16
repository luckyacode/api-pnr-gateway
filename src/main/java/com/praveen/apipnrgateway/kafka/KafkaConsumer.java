package com.praveen.apipnrgateway.kafka;

import com.praveen.apipnrgateway.helper.Utils;
import com.praveen.apipnrgateway.dto.CheckInRequest;
import com.praveen.apipnrgateway.dto.DCSRequest;
import com.praveen.apipnrgateway.dto.PnrRequest;
import com.praveen.apipnrgateway.kafka.events.*;
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

    @KafkaListener(topics = KafkaTopics.PNR_EVENTS, groupId = KafkaGroups.PNR_PROCESSOR_GROUP)
    public void consumingPnrRequest(@Payload String request, @Header(value = KafkaHeaders.RECEIVED_KEY) String pnrId) {
        log.info("✓ Received PnrEvent via Kafka Broker partition. PNR Key: {}, Action: {}", pnrId, request);
        PnrEvent pnrEvent = Utils.jsonToObject(request, PnrEvent.class);
        kafkaService.processPnrMessage(pnrEvent);
    }

    @KafkaListener(topics = KafkaTopics.CheckIn.REQUESTS, groupId = KafkaGroups.DCS_VALIDATION_GROUP)
    public void consumingCheckInRequest(@Payload String request, @Header(value = KafkaHeaders.RECEIVED_KEY) String pnrId) {
        log.info("✓ Received CheckInEvent via Kafka Broker partition. PNR Key: {}, Action: {}", pnrId, request);
        CheckInEvent checkInEvent = Utils.jsonToObject(request, CheckInEvent.class);
        kafkaService.processCheckInMessage(checkInEvent);
    }

    @KafkaListener(topics = KafkaTopics.DCS_EVENTS, groupId = KafkaGroups.DCS_PROCESSOR_GROUP)
    public void consumingDCSRequest(@Payload String request, @Header(value = KafkaHeaders.RECEIVED_KEY) String pnrId) {
        log.info("✓ Received DCSRequestEvent via Kafka Broker partition. PNR Key: {}, Action: {}", pnrId, request);
        DCSRequestEvent dcsRequestEvent = Utils.jsonToObject(request, DCSRequestEvent.class);
        log.info("Successfully Message Received : {}", dcsRequestEvent);
        kafkaService.processDCSMessage(dcsRequestEvent);
    }


}
