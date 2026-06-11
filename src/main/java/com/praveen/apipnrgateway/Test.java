package com.praveen.apipnrgateway;

import com.praveen.apipnrgateway.entity.PNR;

public class Test {
    public static void main(String[] args) {
        String pnr = "{\"id\":\"1\",\"PNRId\":\"1968VU\",\"bookingDateTime\":\"2026-06-11T11:27:27.0356126\",\"bookingStatus\":\"CONFIRMED\",\"flightId\":\"AirAsia-346\",\"passengerId\":7,\"ticketStatus\":\"CONFIRM\",\"bookingChannel\":\"AIRLINE\",\"bookingClass\":\"J\",\"agencyId\":\"AGENT-01\",\"transactionId\":\"641d7dda-12bb-44a8-9e22-e1853a00ede9\",\"totalAmount\":0.1,\"currency\":\"INR\"}";

        System.out.println(Utils.convertToEdifact(Utils.jsonToObject(pnr, PNR.class)));
    }
}
