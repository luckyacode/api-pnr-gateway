package com.praveen.apipnrgateway.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Builder
@Entity
@Table(name = "dcs_flight_manifests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DcsFlightManifest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1. Core Operational Flight Details
//    @Column(nullable = false)
    private String flightId;          // e.g., "AirAsia-346"

//    @Column(nullable = false)
    private String departurePort;     // e.g., "ABJ"

//    @Column(nullable = false)
    private String arrivalPort;       // e.g., "BOY"

//    @Column(nullable = false)
    private String flightDate;        // e.g., "2026-06-12"

    // 2. Flight Weight & Balance Summaries (Calculated dynamically by the DCS)
    private int totalCheckedBags;
    private double totalBaggageWeightKg;

    // 3. One-to-Many Relationship: The actual rows of passengers on this flight
    @Builder.Default
    @OneToMany(mappedBy = "dcsManifest", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DcsPassengerManifest> passengers = new ArrayList<>();

    // 4. Operational Audit Trail
    private LocalDateTime manifestHydratedAt;
    private LocalDateTime finalManifestClosedAt;

    @Builder.Default
    private boolean isManifestClosed = false; // Set to true once the aircraft doors close

    // Helper method to add passengers cleanly maintaining bi-directional sync
    public void addPassenger(DcsPassengerManifest passenger) {
        passengers.add(passenger);
        passenger.setDcsManifest(this);
    }
}