package com.praveen.apipnrgateway.helper;

import com.praveen.apipnrgateway.dto.PNRRequest;
import com.praveen.apipnrgateway.entity.PNR;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommonMapper {

    @Mapping(source = "PNRId",target = "pnrId")
    PNR toPNR(PNRRequest pnrRequest);

}
