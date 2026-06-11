package com.praveen.apipnrgateway.dto;

import java.time.LocalDate;

public record DocumentDetails(String passportNumber,
                              String documentType,
                              LocalDate documentExpiry,
                              String issuingCountry) {
}
