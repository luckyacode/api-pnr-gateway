package com.praveen.apipnrgateway;

import com.praveen.apipnrgateway.entity.PNR;
import tools.jackson.databind.ObjectMapper;

import java.time.format.DateTimeFormatter;
import java.util.Random;

public class Utils {
    public static ObjectMapper objectMapper = new ObjectMapper();
    public static Random random = new Random();
    public static <T> String objectToJson(T object) {
        return objectMapper.writeValueAsString(object);
    }

    public static <T> T jsonToObject(String json, Class<T> targetClass) {
        return objectMapper.readValue(json, targetClass);
    }

    public static int next(int i,int j){
        return random.nextInt(i,j);
    }

    public static double nextDouble(int i,int j){
        return random.nextDouble(i,j);
    }



    public static String convertToEdifact(PNR pnr) {
        StringBuilder edi = new StringBuilder();

        // 1. Clean up the transaction UUID to be under the 35-character UN/EDIFACT limit
        String cleanTransactionId = pnr.getTransactionId() != null ? pnr.getTransactionId().replace("-", "") : "TXUNKNOWN";

        // Format date formats to EDI standards
        String ediDate = pnr.getBookingDateTime().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        String interchangeDate = pnr.getBookingDateTime().format(DateTimeFormatter.ofPattern("yyMMdd:HHmm"));

        // 2. Track segment count dynamically (Starts at 1 for UNH)
        int segmentCount = 0;

        // Synthesize structural segments safely
        edi.append("UNB+UNOA:2+").append(pnr.getAgencyId()).append("+AIRLINE+").append(interchangeDate).append("+REF001'\n");

        edi.append("UNH+1+PAXRST:D:02B:UN'\n"); segmentCount++;
        edi.append("BGM+9+").append(cleanTransactionId).append("+9'\n"); segmentCount++;
        edi.append("DTM+137:").append(ediDate).append(":203'\n"); segmentCount++;
        edi.append("RFF+SND:").append(pnr.getPnrId()).append("'\n"); segmentCount++;

        // Add fallback checks for related entities to prevent NullPointerExceptions
        if (pnr.getFlight() != null) {
            edi.append("TBDT+").append(pnr.getFlight().getFlightId())
                    .append("+").append(pnr.getBookingClass()).append("'\n");
            segmentCount++; // Only increment if the segment is actually written!
        }

        edi.append("MOA+146:").append(String.format(java.util.Locale.US, "%.2f", pnr.getTotalAmount())).append(":").append(pnr.getCurrency()).append("'\n");
        segmentCount++;

        // Include the UNT segment itself in the total count
        segmentCount++;
        edi.append("UNT+").append(segmentCount).append("+1'\n");

        edi.append("UNZ+1+REF001'");

        return edi.toString();
    }



}
