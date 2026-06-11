package com.praveen.apipnrgateway;

import com.praveen.apipnrgateway.dto.PNRRequest;
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

    @KafkaListener(topics = "pnr-topic", groupId = "group-id1")
    public void consumingPNRRequest(@Payload String request, @Header(value = KafkaHeaders.RECEIVED_KEY) String pnrId) {
        log.info("Message received with key {} and message : {}", pnrId, request);
        PNRRequest pnrRequest = Utils.jsonToObject(request, PNRRequest.class);
        log.info("Successfully Message Received : {}", pnrRequest);
        kafkaService.processPNRMessage(pnrRequest);
    }
}
