package com.praveen.apipnrgateway.kafka.events;

public class KafkaGroups {

    private KafkaGroups() {
    }

    public static final String PNR_PROCESSOR_GROUP = "dcs-request-event.avsc-pnr-events-processor-group";
    public static final String DCS_PROCESSOR_GROUP = "dcs-request-event.avsc-core-events-processor-group";
    public static final String DCS_VALIDATION_GROUP = "dcs-request-event.avsc-checkin-request-validator-group";
    public static final String DCS_SIMULATOR_GROUP = "dcs-request-event.avsc-simulator-checkin-response-group";
}