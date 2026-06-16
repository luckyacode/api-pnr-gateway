package com.praveen.apipnrgateway.helper;

import com.praveen.apipnrgateway.dto.PnrRequest;
import com.praveen.apipnrgateway.entity.PNR;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommonMapper {

    PNR toPNR(PnrRequest pnrRequest);

}
