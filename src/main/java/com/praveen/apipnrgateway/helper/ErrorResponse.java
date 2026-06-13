package com.praveen.apipnrgateway.helper;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Data
public class ErrorResponse {
    private String message;
    private String timestamp;
}
