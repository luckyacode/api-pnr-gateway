package com.praveen.apipnrgateway.dto;

import lombok.*;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Data
public class PnrRequest {
    private String pnrId;
    private LocalDateTime bookingDateTime;
    private String bookingStatus;
    private String flightId;
    private Integer passengerId;
    private TicketStatus ticketStatus;
    private Channel bookingChannel;
    private BookingClass bookingClass;
    private String agencyId;
    private String transactionId;
    private Double totalAmount;
    private String currency;
}
