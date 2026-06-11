package com.praveen.apipnrgateway.entity;

import com.praveen.apipnrgateway.dto.DcsStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class DCS {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String flightId;
    private String pnrId;
    private String passengerId;
    private String seatNumber;
    @Enumerated(EnumType.STRING)
    private DcsStatus dcsStatus;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "app_clearance_id")
    private APP appClearance;

    private LocalDateTime updatedByDcsAt;
}

