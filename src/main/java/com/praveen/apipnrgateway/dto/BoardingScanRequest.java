package com.praveen.apipnrgateway.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class BoardingScanRequest {
    private String flightId;
    private String passengerId;
}
