package com.praveen.apipnrgateway;


import com.praveen.apipnrgateway.dto.PNRRequest;
import com.praveen.apipnrgateway.service.PNRService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaService {
    private final PNRService pnrService;
    public void processPNRMessage(PNRRequest pnrRequest){
        log.info("Processing PNR ...{}",pnrRequest.getPNRId());
        pnrService.addPNR(pnrRequest);
    }
}
