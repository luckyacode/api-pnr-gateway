package com.praveen.apipnrgateway.entity;

import com.praveen.apipnrgateway.dto.DocumentDetails;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
public class Passenger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String fullName;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String city;
    private String country;
    @Embedded
    private DocumentDetails documentDetails;
}