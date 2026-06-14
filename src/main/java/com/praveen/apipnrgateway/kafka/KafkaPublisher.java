package com.praveen.apipnrgateway.kafka;

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
    private final KafkaTemplate<String,String> kafkaTemplate;

    public void sendCheckInResponseMessage(String clearanceId, String checkInResponseJson) {
        CompletableFuture<SendResult<String, String>> result = kafkaTemplate.send("checkin-response",clearanceId,checkInResponseJson);
        result.whenComplete((((object, exception) -> {
            if(exception==null)
                log.info("Successfully published kafka message to  : checkin-response");
            else {
                log.error("failed to  published kafka message to  : checkin-response ",exception);
            }
        })));
    }

}
