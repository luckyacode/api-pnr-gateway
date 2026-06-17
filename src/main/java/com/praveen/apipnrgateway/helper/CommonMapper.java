package com.praveen.apipnrgateway.helper;

import com.praveen.airline.avro.AvroCheckInRequest;
import com.praveen.airline.avro.AvroCheckInResponse;
import com.praveen.airline.avro.AvroDcsRequestEvent;
import com.praveen.airline.avro.AvroPnrEvent;
import com.praveen.apipnrgateway.dto.CheckInRequest;
import com.praveen.apipnrgateway.dto.PnrRequest;
import com.praveen.apipnrgateway.entity.PNR;
import com.praveen.apipnrgateway.kafka.events.CheckInEvent;
import com.praveen.apipnrgateway.kafka.events.CheckInResponseEvent;
import com.praveen.apipnrgateway.kafka.events.DCSRequestEvent;
import com.praveen.apipnrgateway.kafka.events.PnrEvent;
import org.mapstruct.Mapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring")
public interface CommonMapper {

    PNR toPNR(PnrRequest pnrRequest);

    PnrRequest toPnrRequest(PnrEvent pnrEvent);

    CheckInRequest toCheckInRequest(CheckInEvent checkInEvent);

    AvroCheckInResponse toAvro(CheckInResponseEvent checkInResponseEvent);

    PnrEvent toPnrEvent(AvroPnrEvent request);

    CheckInEvent toCheckInEvent(AvroCheckInRequest request);

    DCSRequestEvent toDcsRequest(AvroDcsRequestEvent request);

    default LocalDateTime map(Instant instant) {
        if (instant == null) {
            return null;
        }
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}
