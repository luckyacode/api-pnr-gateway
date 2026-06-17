package com.praveen.apipnrgateway.kafka.events;

import com.praveen.apipnrgateway.dto.BookingClass;
import com.praveen.apipnrgateway.dto.Channel;
import com.praveen.apipnrgateway.dto.TicketStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PnrEvent(String pnrId, LocalDateTime bookingDateTime, String bookingStatus, String flightId,
                       Integer passengerId, TicketStatus ticketStatus, Channel bookingChannel,
                       BookingClass bookingClass, String agencyId, String transactionId, Double totalAmount,
                       String currency) {
}
