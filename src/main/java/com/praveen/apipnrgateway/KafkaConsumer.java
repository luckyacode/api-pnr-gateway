package com.praveen.apipnrgateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumer {

    @KafkaListener(topics = "pnr-topic",groupId = "group-id1")
    public void consumingPNRRequest(@Payload PNRRequest pnrRequest){
        log.info("PNRRequest Message recieved at kafka consumer : {}",pnrRequest);
    }
}
