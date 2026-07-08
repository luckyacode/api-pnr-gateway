package com.praveen.apipnrgateway.kafka;

import com.praveen.apipnrgateway.entity.DlqTopic;
import com.praveen.apipnrgateway.helper.AirlineException;
import com.praveen.apipnrgateway.helper.CommonMapper;
import com.praveen.apipnrgateway.kafka.events.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {

    private final KafkaService kafkaService;
    private final CommonMapper mapper;

    @RetryableTopic(attempts = "3",traversingCauses = "true",exclude = {AirlineException.class})
    @KafkaListener(topics = KafkaTopics.PNR_EVENTS, groupId = KafkaGroups.PNR_PROCESSOR_GROUP)
    public void consumingPnrRequest(@Payload PnrEvent request, @Header(value = KafkaHeaders.RECEIVED_KEY) String pnrId, Acknowledgment ack) {
        log.info("✓ Received PnrEvent via Kafka Broker partition. PNR Key: {}, Action: {}", pnrId, request);
        try {
//             PnrEvent pnrEvent = mapper.toPnrEvent(request);
            kafkaService.processPnrMessage(request);
            ack.acknowledge();
        } catch (AirlineException ex) {
            log.warn("⚠️ Business rule violation for PNR {}. Skipping retries. Reason: {}", pnrId, ex.getMessage());
            ack.acknowledge();
        }
    }

    @RetryableTopic(attempts = "3",traversingCauses = "true",exclude = {AirlineException.class})
    @KafkaListener(topics = KafkaTopics.CheckIn.REQUESTS, groupId = KafkaGroups.DCS_VALIDATION_GROUP)
    public void consumingCheckInRequest(@Payload CheckInEvent request, @Header(value = KafkaHeaders.RECEIVED_KEY) String pnrId, Acknowledgment ack) {
        log.info("✓ Received CheckInEvent via Kafka Broker partition. PNR Key: {}, Action: {}", pnrId, request);
        try {
//            CheckInEvent checkInEvent = mapper.toCheckInEvent(request);
            kafkaService.processCheckInMessage(request);
            log.info("check in request consume {}",request);
            ack.acknowledge();
        } catch (AirlineException ex) {
            log.warn("⚠️ Business rule violation for PNR {}. Skipping retries. Reason: {}", pnrId, ex.getMessage());
            ack.acknowledge();
        }

}

    @RetryableTopic(attempts = "3",traversingCauses = "true",exclude = {AirlineException.class})
    @KafkaListener(topics = KafkaTopics.DCS_EVENTS, groupId = KafkaGroups.DCS_PROCESSOR_GROUP)
    public void consumingDCSRequest(@Payload DCSRequestEvent request, @Header(value = KafkaHeaders.RECEIVED_KEY) String pnrId, Acknowledgment ack) {
        log.info("✓ Received DCSRequestEvent via Kafka Broker partition. PNR Key: {}, Action: {}", pnrId, request);
        try{
//            DCSRequestEvent dcsRequestEvent = mapper.toDcsRequest(request);
            log.info("Successfully Message Received : {}", request);
            kafkaService.processDCSMessage(request);
            log.info("request : {}",request);
            ack.acknowledge();
        } catch (AirlineException ex){
            log.warn("⚠️ Business rule violation for PNR {}. Skipping retries. Reason: {}", pnrId, ex.getMessage());
            ack.acknowledge();
        }
    }

    @DltHandler
    public void handleDlt(
            @Payload Object failedEvent,
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
//                    .event(failedEvent)
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
