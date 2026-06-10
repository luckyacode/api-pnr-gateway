package com.praveen.apipnrgateway;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
//@Builder
@Data
public class DynamicFlight {
    // Static fields from your original dataset
    private String airline;
    private String sourceAirport;
    private String departureCountry;
    private String destAirport;
    private String arrivalCountry;
    private String equipment;

    // Dynamically generated schedule fields
    private String flightId;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private String status; // e.g., On Time, Delayed, Boarding

    // Constructor mapping raw route data to dynamic objects
    public DynamicFlight(String[] csvRow) {
        // Map columns from: airline, source_airport, departure_airport_name, departure_city, departure_country, dest_airport...
        this.airline = csvRow[0];
        this.sourceAirport = csvRow[1];
        this.departureCountry = csvRow[4];
        this.destAirport = csvRow[5];
        this.arrivalCountry = csvRow[8];
        this.equipment = csvRow[9];

        // Trigger the dynamic engine for this flight instance
        generateDynamicSchedule();
    }

    private void generateDynamicSchedule() {
        // 1. Generate Flight ID
        int randomNum = (int) (Math.random() * 900) + 100;
        this.flightId = this.airline + "-" + randomNum;

        // 2. Generate Departure Time for "Today"
        int hour = (int) (Math.random() * 24);
        int[] minutes = {0, 15, 30, 45};
        int minute = minutes[(int) (Math.random() * minutes.length)];

        // Use LocalDateTime instead of just LocalTime
        LocalDateTime departureDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(hour, minute));
        this.departureTime = departureDateTime.toLocalTime(); // Kept for your current print statement

        // 3. Generate Flight Duration (e.g., 9 hours)
        int flightDurationHours = (int) (Math.random() * 10) + 2;

        // Let Java automatically handle rolling over past midnight to the next day
        LocalDateTime arrivalDateTime = departureDateTime.plusHours(flightDurationHours);
        this.arrivalTime = arrivalDateTime.toLocalTime();

        // 4. Check if the flight landed on the next day
        boolean isNextDay = arrivalDateTime.toLocalDate().isAfter(departureDateTime.toLocalDate());

        // 5. Generate Live Status (Append '+1' if it's an overnight cross-border flight)
        String[] statuses = {"ON TIME", "ON TIME", "DELAYED", "BOARDING"};
        String baseStatus = statuses[(int) (Math.random() * statuses.length)];
        this.status = isNextDay ? baseStatus + " (+1 DAY)" : baseStatus;
    }

//    private void generateDynamicSchedule() {
//        // 1. Generate Flight ID (e.g., AA-742)
//        int randomNum = (int) (Math.random() * 900) + 100; // 100 to 999
//        this.flightId = this.airline + "-" + randomNum;
//
//        // 2. Generate Departure Time (randomized throughout the day on blocks of 15 mins)
//        int hour = (int) (Math.random() * 24);
//        int[] minutes = {0, 15, 30, 45};
//        int minute = minutes[(int) (Math.random() * minutes.length)];
//        this.departureTime = LocalTime.of(hour, minute);
//
//        // 3. Generate Arrival Time (adding a random flight duration of 2 to 11 hours)
//        int flightDurationHours = (int) (Math.random() * 10) + 2;
//        this.arrivalTime = this.departureTime.plusHours(flightDurationHours);
//
//        // 4. Generate Live Status
//        String[] statuses = {"ON TIME", "ON TIME", "ON TIME", "DELAYED", "BOARDING"};
//        this.status = statuses[(int) (Math.random() * statuses.length)];
//    }

    @Override
    public String toString() {
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
        return String.format("[%s] %s | %s (%s) %s -> %s (%s) %s | %s",
                status, flightId, sourceAirport, departureCountry, departureTime.format(timeFormat),
                destAirport, arrivalCountry, arrivalTime.format(timeFormat), equipment);
    }
}