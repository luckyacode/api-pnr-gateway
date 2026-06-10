package com.praveen.apipnrgateway;


import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class BookingConfirmation {
    private String pnrCode;
    private Passenger passenger;
    private DynamicFlight flight;

    public BookingConfirmation(Passenger passenger, DynamicFlight flight) {
        this.passenger = passenger;
        this.flight = flight;
        this.pnrCode = generateUniquePNR();
    }

    private String generateUniquePNR() {
        String alphaNumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            builder.append(alphaNumeric.charAt((int) (Math.random() * alphaNumeric.length())));
        }
        return builder.toString();
    }

    // NEW: Real-life EDIFACT Translation Matrix
    public String toEdifact() {
        DateTimeFormatter edifactDateFormat = DateTimeFormatter.ofPattern("ddMMyy");
        DateTimeFormatter edifactTimeFormat = DateTimeFormatter.ofPattern("HHmm");

        StringBuilder edifact = new StringBuilder();

        // UNB: Interchange Header (Sender ID, Receiver ID, Timestamp)
        edifact.append("UNB+IATB:1+GDSAMADEUS+AIRLINEBACKEND+260610:0752+1'\n");

        // UNH: Message Header (Message Reference Number, PNRGOV standard type)
        edifact.append("UNH+1+PNRGOV:UN:D:11B:IATA'\n");

        // ORG: Originator of the booking (GDS system + PNR validation code)
        edifact.append(String.format("ORG+1A+PARIS1A0001+++%s'\n", pnrCode));

        // TVL: Travel Product Segment (Departure Date, Flight ID, Source Airport -> Dest Airport)
        edifact.append(String.format("TVL+%s:%s+%s+%s+%s++%s'\n",
                flight.getDepartureDate().format(edifactDateFormat),
                flight.getDepartureTime().format(edifactTimeFormat),
                flight.getSourceAirport(),
                flight.getDestAirport(),
                flight.getAirline(),
                flight.getFlightId()
        ));

        // TBD: True Border Breakdown Data (Target Arrival Window)
        edifact.append(String.format("TBD+%s:%s'\n",
                flight.getArrivalDate().format(edifactDateFormat),
                flight.getArrivalTime().format(edifactTimeFormat)
        ));

        // ATT: Equipment Attribute
        edifact.append(String.format("ATT+2++%s'\n", flight.getEquipment()));

        // TIF: Traveler Information (Last Name, First Name)
        edifact.append(String.format("TIF+%s+%s'\n",
                passenger.getLastName().toUpperCase(),
                passenger.getFirstName().toUpperCase()
        ));

        // NAT: Nationality details mapped from passenger profile
        edifact.append(String.format("NAT+2+%s'\n", passenger.getCountry().substring(0, Math.min(passenger.getCountry().length(), 3)).toUpperCase()));

        // COM: Communication details (Phone / Email contact tags)
        edifact.append(String.format("COM+%s:TE'\n", passenger.getPhone()));
        edifact.append(String.format("COM+%s:EM'\n", passenger.getEmail()));

        // UNT & UNZ: Message and Interchange Trailers (Checks counts for data integrity)
        edifact.append("UNT+10+1'\n");
        edifact.append("UNZ+1+1'");

        return edifact.toString();
    }

    @Override
    public String toString() {
        return String.format("🎟️ PNR: %s | PASSENGER: %s | FLIGHT: %s",
                pnrCode, passenger.getFullName(), flight.toString());
    }
}