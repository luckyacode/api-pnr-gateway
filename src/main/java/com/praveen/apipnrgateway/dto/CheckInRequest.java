package com.praveen.apipnrgateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckInRequest {
    private String pnrId;
    private String clearanceId;
    private DocumentDetails documentDetails;
}
