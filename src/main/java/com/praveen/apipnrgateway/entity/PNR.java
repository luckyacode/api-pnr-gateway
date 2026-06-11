package com.praveen.apipnrgateway.entity;


import com.praveen.apipnrgateway.dto.BookingClass;
import com.praveen.apipnrgateway.dto.Channel;
import com.praveen.apipnrgateway.dto.TicketStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
public class PNR {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String pnrId;
    @OneToOne
    @JoinColumn(name = "passenger_id")
    private Passenger passenger;
    @ManyToOne
    @JoinColumn(name = "flight_id")
    private FlightManifest flight;
    @Enumerated(value = EnumType.STRING)
    private TicketStatus ticketStatus;
    @Enumerated(value = EnumType.STRING)
    private Channel bookingChannel;
    private LocalDateTime bookingDateTime;
    private BookingClass bookingClass;
    private String agencyId;
    private String bookingStatus;
    private String transactionId;
    private double totalAmount;
    private String currency;
}