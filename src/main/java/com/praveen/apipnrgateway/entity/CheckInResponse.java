package com.praveen.apipnrgateway.entity;

import com.praveen.apipnrgateway.dto.GovernmentClearanceResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class CheckInResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Embedded
    private GovernmentClearanceResponse governmentClearanceResponse;
}
