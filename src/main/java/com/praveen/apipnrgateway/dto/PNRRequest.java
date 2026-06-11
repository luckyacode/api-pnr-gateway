package com.praveen.apipnrgateway.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Data
public class PNRRequest {
    private String PNRId;
    private LocalDateTime bookingDateTime;
    private String bookingStatus;
    private String flightId;
    private int passengerId;
    private TicketStatus ticketStatus;
    private Channel bookingChannel;
    private BookingClass bookingClass;
    private String agencyId;
    private String transactionId;
    private double totalAmount;
    private String currency;
}
