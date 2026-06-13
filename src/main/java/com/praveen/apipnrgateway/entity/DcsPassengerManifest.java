package com.praveen.apipnrgateway.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.praveen.apipnrgateway.dto.DcsStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "dcs_passenger_manifests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DcsPassengerManifest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link back to the Master Flight Manifest Aggregate
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_manifest_id")
    @JsonIgnoreProperties("passengers")
    private DcsFlightManifest dcsManifest;

    // Core Identity & PNR Binding
//    @Column(nullable = false)
    private String pnrId;

//    @Column(nullable = false)
    private String passengerId;

    private String passengerName;

    // Operational Airport Parameters
    private String seatNumber;        // e.g., "12B"
    private int sequenceNumber;       // Chronological boarding scan sequence order

    @Builder.Default
    private int baggageCount = 0;

    @Builder.Default
    private double totalBagWeight = 0.0;

    private String specialServiceRequest; // SSR Codes like "WCHR" (Wheelchair), "VGML" (Vegan Meal)

    // Current Life-cycle Status of Passenger at Airport
    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
    private DcsStatus dcsStatus;

    // Regulatory Government Compliance Reference
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "app_clearance_id")
    private APP appClearance;

    private LocalDateTime lastUpdatedTime;
}