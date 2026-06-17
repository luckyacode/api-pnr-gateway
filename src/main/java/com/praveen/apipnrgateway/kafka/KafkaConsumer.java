package com.praveen.apipnrgateway.kafka;

import com.praveen.apipnrgateway.entity.DlqTopic;
import com.praveen.apipnrgateway.helper.AirlineException;
import com.praveen.apipnrgateway.helper.Utils;
import com.praveen.apipnrgateway.dto.CheckInRequest;
import com.praveen.apipnrgateway.dto.DCSRequest;
import com.praveen.apipnrgateway.dto.PnrRequest;
import com.praveen.apipnrgateway.kafka.events.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {

    private final KafkaService kafkaService;

    @RetryableTopic(attempts = "3",traversingCauses = "true",exclude = {AirlineException.class})
    @KafkaListener(topics = KafkaTopics.PNR_EVENTS, groupId = KafkaGroups.PNR_PROCESSOR_GROUP)
    public void consumingPnrRequest(@Payload String request, @Header(value = KafkaHeaders.RECEIVED_KEY) String pnrId) {
        log.info("✓ Received PnrEvent via Kafka Broker partition. PNR Key: {}, Action: {}", pnrId, request);
        PnrEvent pnrEvent = Utils.jsonToObject(request, PnrEvent.class);
        kafkaService.processPnrMessage(pnrEvent);
    }

    @RetryableTopic(attempts = "3",traversingCauses = "true",exclude = {AirlineException.class})
    @KafkaListener(topics = KafkaTopics.CheckIn.REQUESTS, groupId = KafkaGroups.DCS_VALIDATION_GROUP)
    public void consumingCheckInRequest(@Payload String request, @Header(value = KafkaHeaders.RECEIVED_KEY) String pnrId) {
        log.info("✓ Received CheckInEvent via Kafka Broker partition. PNR Key: {}, Action: {}", pnrId, request);
        CheckInEvent checkInEvent = Utils.jsonToObject(request, CheckInEvent.class);
        kafkaService.processCheckInMessage(checkInEvent);
    }

    @RetryableTopic(attempts = "3",traversingCauses = "true",exclude = {AirlineException.class})
    @KafkaListener(topics = KafkaTopics.DCS_EVENTS, groupId = KafkaGroups.DCS_PROCESSOR_GROUP)
    public void consumingDCSRequest(@Payload String request, @Header(value = KafkaHeaders.RECEIVED_KEY) String pnrId) {
        log.info("✓ Received DCSRequestEvent via Kafka Broker partition. PNR Key: {}, Action: {}", pnrId, request);
        DCSRequestEvent dcsRequestEvent = Utils.jsonToObject(request, DCSRequestEvent.class);
        log.info("Successfully Message Received : {}", dcsRequestEvent);
        kafkaService.processDCSMessage(dcsRequestEvent);
    }

    @DltHandler
    public void handleDlt(
            @Payload String failedEvent,
            @Header(KafkaHeaders.RECEIVED_KEY) String pnrId,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String deadTopic,
            @Header(KafkaHeaders.EXCEPTION_MESSAGE) String errorMessage) {


        log.error("🚨🛑 CRITICAL INTERCEPT: Appending message error signature to DB Triage Log table.");

        log.error("-> Partition Key: {} | Failed Topic: {}", pnrId, deadTopic);
        log.error("-> Failed PNR Locator: {}", pnrId);
        log.error("-> Source Dead Topic : {}", deadTopic);
        log.error("-> Failure Reason    : {}", errorMessage);
        log.error("-> Payload    : {}", failedEvent);


        try {
            // 1. Resolve true error description strings safely
            String finalReason = "Unknown Operational Exception";
          if (errorMessage != null) {
                finalReason = errorMessage;
            }
            if (finalReason != null && finalReason.length() > 1000) {
                finalReason = finalReason.substring(0, 995) + "...";
            }

            DlqTopic dlqTopic = DlqTopic.builder()
                    .pnrId(pnrId != null ? pnrId : "NO_PNR")
                    .sourceTopic(deadTopic)
                    .deadLetterTopic(deadTopic)
                    .reason(finalReason)
                    .event(failedEvent)
                    .loggedAt(Instant.now())
                    .resolved(Boolean.FALSE)
                    .build();

            kafkaService.saveDlq(dlqTopic);
            log.info("✓ DLQ Message Record committed in database cleanly.");

        } catch (Exception dbEx) {
            // 🌟 CRITICAL FAILSAFE: If your database table fails to save, log it to standard error
            // but DO NOT throw the exception back up. Let the Kafka thread acknowledge and advance offsets!
            log.error("💥 SYSTEM EMERGENCY: Failed to write triage log to PostgreSQL database. " +
                    "Aborting save to prevent consumer blockages. Error details: {}", dbEx.getMessage());
        }
    }

}
