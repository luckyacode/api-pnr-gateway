package com.praveen.apipnrgateway.helper;

import com.praveen.apipnrgateway.dto.CheckInRequest;
import com.praveen.apipnrgateway.dto.PnrRequest;
import com.praveen.apipnrgateway.entity.PNR;
import com.praveen.apipnrgateway.kafka.events.CheckInEvent;
import com.praveen.apipnrgateway.kafka.events.PnrEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommonMapper {

    PNR toPNR(PnrRequest pnrRequest);

    PnrRequest toPnrRequest(PnrEvent pnrEvent);

    CheckInRequest toCheckInRequest(CheckInEvent checkInEvent);
}
