package com.praveen.apipnrgateway;

import lombok.Builder;


/**
 * Represents an industry-standard Passenger Manifest Data Vector
 * conforming to IATA PNRGOV / PADIS implementation guides.
 */
//Passenger ID,First Name,Last Name,Gender,Age,Nationality,Airport Name,Airport Country Code,Country Name,Airport Continent,Continents,Departure Date,Arrival Airport,Pilot Name,Flight Status

@Builder
public record PassengerManifest(String uniquePassengerReference, // Map from Passenger ID
                                String firstName,                // Map from First Name
                                String lastName,                  // Map from Last Name
                                String gender, int age, String nationality,   // Map from Nationality
                                String departureAirport,         // Updated field name
                                String arrivalAirportCountryCode,// Map from Airport Country Code
                                String arrivalAirportCountryName,// Map from Country Name
                                String destinationRegionCode,    // Map from Airport Continent
                                String destinationRegionName,    // Map from Continents
                                String scheduledDepartureDate,   // Map from Departure Date
                                String arrivalAirport,           // Updated field name (e.g., "CXF")
                                String pilotInCommand,           // Map from Pilot Name
                                String operationalStatus         // Map from Flight Status
) {
}