package com.praveen.apipnrgateway.kafka.events;

public class KafkaGroups {

    private KafkaGroups() {
    }

    public static final String PNR_PROCESSOR_GROUP = "dcs-pnr-events-processor-group";
    public static final String DCS_PROCESSOR_GROUP = "dcs-core-events-processor-group";
    public static final String DCS_VALIDATION_GROUP = "dcs-checkin-request-validator-group";
    public static final String DCS_SIMULATOR_GROUP = "dcs-simulator-checkin-response-group";
}