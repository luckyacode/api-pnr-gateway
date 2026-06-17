package com.praveen.apipnrgateway.kafka.events;

import com.praveen.apipnrgateway.dto.GovernmentClearanceResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckInResponseEvent {
        int id;
        String pnrId;
    GovernmentClearanceResponse governmentClearanceResponse;

}
