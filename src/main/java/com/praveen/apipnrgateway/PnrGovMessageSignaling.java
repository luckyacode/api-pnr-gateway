//package com.praveen.apipnrgateway;
//
//import java.time.LocalDate;
//import java.time.ZonedDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Locale;
//import java.util.Random;
//import java.util.stream.Collectors;
//
//public class PnrGovMessageSignaling {
//
//    private static final String IATA_RECORD_LOCATOR_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
//    private static final Random RANDOM = new Random();
//
//    private String generateAmadeusRecordLocator() {
//        return RANDOM.ints(6, 0, IATA_RECORD_LOCATOR_CHARS.length())
//                .mapToObj(IATA_RECORD_LOCATOR_CHARS::charAt)
//                .map(Object::toString)
//                .collect(Collectors.joining());
//    }
//
//    public String serializeToPnrGovEdifact(PassengerManifest manifest) {
//        String recordLocator = generateAmadeusRecordLocator();
//        String currentUtcTimestamp = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd:HHmm", Locale.ENGLISH));
//
//        // Parse Scheduled Departure Date
//        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("M/d/yyyy", Locale.ENGLISH);
//        LocalDate sddt = LocalDate.parse(manifest.scheduledDepartureDate(), inputFormat);
//        String formattedSddt = sddt.format(DateTimeFormatter.ofPattern("yyMMdd", Locale.ENGLISH));
//
////        String operatingCarrier = "AA"; // Standard system default placeholder
//        String surname = manifest.lastName().toUpperCase().trim();
//        String givenName = manifest.firstName().toUpperCase().trim();
//        String arrivalCountry = manifest.arrivalStationCountryCode().toUpperCase().trim();
//        String destinationStation = manifest.arrivalStationCode().toUpperCase().trim();
//        String operationalStatus = manifest.operationalStatus().toUpperCase().trim();
//
//        StringBuilder edifactPayload = new StringBuilder();
//
//        // UNA & UNB: Interchange Enveloping Protocols
//        edifactPayload.append("UNA:+.?*'\n");
//        edifactPayload.append(String.format("UNB+IATA:1+%s+%s+%s+MSG011+PNRGOV'\n", operatingCarrier, arrivalCountry, currentUtcTimestamp));
//
//        // UNH: Message Header Segment
//        edifactPayload.append("UNH+1+PNRGOV:11:1:IA'\n");
//
//        // SRC: Segment Source (Identifies Carrier and the Reservation Record Locator Code)
//        edifactPayload.append(String.format("SRC+1+%s+%s'\n", operatingCarrier, recordLocator));
//
//        // TVL: Travel Product Information (Core flight routing details)
//        edifactPayload.append(String.format("TVL+%s:1200+%s:%s+999+%s+++A'\n",
//                formattedSddt, manifest.departureStationName().toUpperCase(), destinationStation, operatingCarrier));
//
//        // DTM: Date/Time/Reference (125 = Scheduled Departure Time Vector)
//        edifactPayload.append(String.format("DTM+125:%s:203'\n", formattedSddt + ":1200"));
//
//        // ATT: Attribute Segment (Function 2 = Operational/Flight Status tracking)
//        edifactPayload.append(String.format("ATT+2++%s'\n", operationalStatus));
//
//        // TBD: Traveler Border Description (Maps core identity)
//        edifactPayload.append(String.format("TBD+1+7+++ADT+++%s:%s'\n", surname, givenName));
//
//        // ATT: Attribute Segment (Function 1 = Demographic data mapping like Gender and Age)
//        edifactPayload.append(String.format("ATT+1++%s++AGE:%d'\n", manifest.gender().toUpperCase(), manifest.age()));
//
//        // NAD: Name and Address (MS = Document/Message Subject nationality context)
//        edifactPayload.append(String.format("NAD+MS+++%s++++%s'\n", (givenName + " " + surname), manifest.nationalityCountryCode().toUpperCase()));
//
//        // UNT & UNZ: Message and Interchange Control Trailers
//        edifactPayload.append("UNT+9+1'\n");
//        edifactPayload.append("UNZ+1+MSG011'");
//
//        return edifactPayload.toString();
//    }
//}