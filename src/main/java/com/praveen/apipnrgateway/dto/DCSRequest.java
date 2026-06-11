package com.praveen.apipnrgateway.dto;

import lombok.*;

@Getter
@Setter
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DCSRequest {
    private String flightId;
    private String pnrId;
    private String passengerId;
}
