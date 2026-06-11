package com.praveen.apipnrgateway.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Data
public class PNRRequest {
    private String PNRId;
    private String bookingDateTime;
    private String bookingStatus;
    private String flightId;
    private String passengerId;
    private TicketStatus ticketStatus;
    private Channel bookingChannel;
    private BookingClass bookingClass;
    private String agencyId;
    private String transactionId;
    private double totalAmount;
    private String currency;
}
