package com.praveen.apipnrgateway;

import com.praveen.apipnrgateway.dto.PNRRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumer {

    @KafkaListener(topics = "pnr-topic",groupId = "group-id1")
    public void consumingPNRRequest(String request, @Header(value = KafkaHeaders.RECEIVED_KEY) String pnrId){
        log.info("Message key recieved : {}",pnrId);
        log.info("PNRRequest Message recieved at kafka consumer : {}",request);
        PNRRequest pnrRequest = Utils.jsonToObject(request,PNRRequest.class);
        log.info("Successfull Message Recived : {}",pnrRequest);
    }
}
