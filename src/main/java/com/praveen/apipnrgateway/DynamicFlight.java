package com.praveen.apipnrgateway;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Data
public class DynamicFlight {
    // Structural vectors from your CSV dataset
    private String airline;
    private String sourceAirport;
    private String departureCountry;
    private String destAirport;
    private String arrivalCountry;
    private String equipment;

    // BRAND NEW: Separated Date and Time Tracking
    private LocalDate departureDate;
    private LocalTime departureTime;
    private LocalDate arrivalDate;
    private LocalTime arrivalTime;
    private String flightId;
    private String status;

    public DynamicFlight(String[] csvRow) {
        this.airline = csvRow[0];
        this.sourceAirport = csvRow[1];
        this.departureCountry = csvRow[4];
        this.destAirport = csvRow[5];
        this.arrivalCountry = csvRow[8];
        this.equipment = csvRow[9];

        // Execute the programmatic dynamic scheduling sequence
        generateFullSchedule();
    }

    private void generateFullSchedule() {
        // 1. Unique Flight identifier
        int randomNum = (int) (Math.random() * 900) + 100;
        this.flightId = this.airline + "-" + randomNum;

        // 2. Schedule Departure (For example, assume flights are generated for "Today")
        int hour = (int) (Math.random() * 24);
        int[] minutes = {0, 15, 30, 45};
        int minute = minutes[(int) (Math.random() * minutes.length)];

        // Combine into a full timestamp anchor
        LocalDateTime departureTimestamp = LocalDateTime.of(LocalDate.now(), LocalTime.of(hour, minute));

        // Split and assign to separate date and time properties
        this.departureDate = departureTimestamp.toLocalDate();
        this.departureTime = departureTimestamp.toLocalTime();

        // 3. Dynamically append random flight duration (between 2 and 14 hours)
        int flightDurationHours = (int) (Math.random() * 12) + 2;

        // Java automatically handles rolling over the hours and increments the date if it crosses midnight
        LocalDateTime arrivalTimestamp = departureTimestamp.plusHours(flightDurationHours);

        // Split and assign to separate arrival date and time properties
        this.arrivalDate = arrivalTimestamp.toLocalDate();
        this.arrivalTime = arrivalTimestamp.toLocalTime();

        // 4. Evaluate status attributes
        String[] statuses = {"ON TIME", "DELAYED", "BOARDING"};
        this.status = statuses[(int) (Math.random() * statuses.length)];
    }

    @Override
    public String toString() {
        // Format layout patterns for structural console outputs
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

        return String.format("[%s] %s | %s (%s) DEP: %s @ %s -> %s (%s) ARR: %s @ %s | Type: %s",
                status, flightId,
                sourceAirport, departureCountry, departureDate.format(dateFormat), departureTime.format(timeFormat),
                destAirport, arrivalCountry, arrivalDate.format(dateFormat), arrivalTime.format(timeFormat),
                equipment);
    }
}