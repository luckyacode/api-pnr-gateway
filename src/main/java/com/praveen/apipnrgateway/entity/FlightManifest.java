package com.praveen.apipnrgateway.entity;

import com.praveen.apipnrgateway.dto.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class FlightManifest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String flightId;
    private String airline;
    private String departureAirport;
    private String departureCountry;
    private String arrivalAirport;
    private String arrivalCountry;
    private LocalDate departureDate;
    private LocalTime departureTime;
    private LocalDate arrivalDate;
    private LocalTime arrivalTime;
    private LocalDateTime scheduledDepartureDateTime;
    private LocalDateTime scheduledArrivalDateTime;
    @Enumerated(value = EnumType.STRING)
    private Status status;
}