//package com.praveen.apipnrgateway;
//
//import java.time.LocalDateTime;
//
//public class Practice {
//    public static void main(String[] args) {
//
//        // 1. Construct Mock Itinerary Data
//        FlightDetails flight = new FlightDetails(
//                "EK",
//                "512",
//                "IND",
//                "KWT",
//                LocalDateTime.of(2026, 6, 15, 10, 30), // 15 June 2026, 10:30 AM
//                LocalDateTime.of(2026, 6, 15, 14, 45)  // 15 June 2026, 02:45 PM
//        );
//
//        // 2. Assemble Master Payload Record
//        PNR transaction = new PNR(
//                1L,
//                "Kim Berry",
//                "Chris",
//                "Russell",
//                "vhill@example.net",
//                "633-913-4354",
//                "Edwardsstad",
//                "Kuwait",
//                flight
//        );
//
//        // 3. Process Execution & Text Generation
//        EdifactPnrGenerator pipeline = new EdifactPnrGenerator();
//        String outboundMessage = pipeline.convertToEdifact(transaction);
//
//        System.out.println(outboundMessage);
//    }
//}