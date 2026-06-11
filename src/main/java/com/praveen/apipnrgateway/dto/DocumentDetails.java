package com.praveen.apipnrgateway.dto;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Data
@Embeddable
public class DocumentDetails {
    private String passportNumber;
    private String documentType;
    private LocalDate documentExpiry;
    private String issuingCountry;
}