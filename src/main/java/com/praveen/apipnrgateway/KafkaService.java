package com.praveen.apipnrgateway;


import com.praveen.apipnrgateway.dto.CheckInRequest;
import com.praveen.apipnrgateway.dto.GovernmentClearanceResponse;
import com.praveen.apipnrgateway.dto.PNRRequest;
import com.praveen.apipnrgateway.entity.CheckInResponse;
import com.praveen.apipnrgateway.entity.PNR;
import com.praveen.apipnrgateway.service.PNRService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaService {
    private final KafkaTemplate<String,String>  kafkaTemplate;
    private final PNRService pnrService;
    private final GovernmentSimulatorService governmentSimulatorService;
    private final KafkaPublisher kafkaPublisher;
    public void processPNRMessage(PNRRequest pnrRequest){
        log.info("Processing PNR ...{}",pnrRequest.getPNRId());
        pnrService.addPNR(pnrRequest);
    }

    @SneakyThrows
    public void processCheckInMessage(CheckInRequest checkInRequest) {
        log.info("Processing CheckIn ...{}",checkInRequest);
        PNR pnr = pnrService.getPNRById(checkInRequest.getPnrId());
        GovernmentClearanceResponse  governmentClearanceResponse = governmentSimulatorService.processClearance(checkInRequest,pnr);
        CheckInResponse checkInResponse = CheckInResponse.builder().
                governmentClearanceResponse(governmentClearanceResponse).build();
        String checkInResponseJson = Utils.objectToJson(checkInResponse);
        kafkaPublisher.sendCheckInResponseMessage(governmentClearanceResponse.getClearanceId(),checkInResponseJson);
    }
}
