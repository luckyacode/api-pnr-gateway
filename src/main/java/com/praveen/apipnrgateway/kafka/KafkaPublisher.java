package com.praveen.apipnrgateway.kafka;

import com.praveen.apipnrgateway.helper.Utils;
import com.praveen.apipnrgateway.kafka.events.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaPublisher {
    private final KafkaTemplate<String,Object> kafkaTemplate;


    public void sendKafkaEvent(String topic, String pnrId, Object eventPayload) {
        if (pnrId == null || pnrId.isBlank()) {
            log.error("💥 SYSTEM BLOCK: Attempted to publish event to topic [{}] without a valid PNR key! Payload aborted to prevent out-of-order state corruption.", topic);
            throw new IllegalArgumentException("Kafka partition routing key (pnrId) cannot be null or empty for stateful changes.");
        }

        log.debug("Routing stateful event to topic [{}] pinned to partition key [PNR: {}]", topic, pnrId);
        kafkaTemplate.send(topic, pnrId, eventPayload).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("🚨 KAFKA BROKER FAILURE: PNR [{}] transaction failed to append. Error: {}", pnrId, ex.getMessage());
            } else {
                log.info("✓ Event committed on topic {} to partition {} at offset {} for PNR: {}", topic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset(), pnrId);
            }
        });
    }

}
