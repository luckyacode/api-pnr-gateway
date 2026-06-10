package com.praveen.apipnrgateway;

import java.io.IOException;

public class MainApplication2 {
    public static void main(String[] args) {
        BookingService bookingEngine = new BookingService();

        try {
            // Point files to your local environment generation paths
            bookingEngine.loadFlights("files/international_flight_traffic.csv");
            bookingEngine.loadPassengers("files/people.csv");

            // Process PNR links programmatically
            bookingEngine.createTestBookings();

            System.out.println("\n--- 📝 COMPLETED REGISTRATION & PNR MANIFEST ---");
            bookingEngine.displayManifest();


        } catch (IOException e) {
            System.err.println("Execution halted. Unable to construct streaming operations: " + e.getMessage());
        }
    }
}