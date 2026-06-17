package com.praveen.apipnrgateway.kafka.events;

import com.praveen.apipnrgateway.dto.DocumentDetails;
import lombok.Builder;

@Builder
public record CheckInEvent(String pnrId, String clearanceId, DocumentDetails documentDetails) {
}
