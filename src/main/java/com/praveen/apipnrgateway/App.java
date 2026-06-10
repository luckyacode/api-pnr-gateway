package com.praveen.apipnrgateway;

import java.io.IOException;
import java.util.List;

public class App {
    public static void main(String[] args) {
        FlightScheduleService engine = new FlightScheduleService();

        try {
            // Path to your base file
            String baseFile = "files/international_flight_traffic.csv";

            // Generate the randomized engine data
            engine.generateSchedules(baseFile);

            // Simulation: User searches for flights from "France" to "United States"
            System.out.println("\n--- ✈️ LIVE DEPARTURE BOARD: France -> United States (Sorted Chronologically) ---");
            List<DynamicFlight> liveResults = engine.getLiveSchedulesBetweenCountries("France", "United States");

            if(liveResults.isEmpty()) {
                System.out.println("No scheduled flights found for this route today.");
            } else {
                liveResults.forEach(System.out::println);
            }

        } catch (IOException e) {
            System.err.println("Fatal: Could not initialize route streams. " + e.getMessage());
        }
    }
}