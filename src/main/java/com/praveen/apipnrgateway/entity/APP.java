package com.praveen.apipnrgateway.entity;

import com.praveen.apipnrgateway.dto.AppProcessingStatus;
import com.praveen.apipnrgateway.dto.GovernmentClearanceResponse;
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
@ToString
public class APP {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String appId;
    private String pnrId;
    private String flightId;
    private String departurePort;  // e.g., "DEL"
    private String arrivalPort;    // e.g., "SIN"
    private String passportNumber;
    private LocalDateTime createdDateTime;
    @Embedded
    private GovernmentClearanceResponse governmentClearanceResponse;
    @Enumerated(EnumType.STRING)
    private AppProcessingStatus processingStatus;
}