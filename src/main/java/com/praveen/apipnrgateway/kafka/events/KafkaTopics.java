package com.praveen.apipnrgateway.kafka.events;

public final class KafkaTopics {

    private KafkaTopics() {}

    public static final String PNR_EVENTS = "passenger.pnr-events.v1";
    public static final String DCS_EVENTS = "passenger.dcs-request-event.avsc-events.v1";

    public static final class CheckIn {
        public static final String REQUESTS  = "checkin.requests.v1";
        public static final String RESPONSES = "checkin.responses.v1";
    }

    public static final class Notification {
        public static final String EMAIL = "notification.email.v1";
        public static final String SMS   = "notification.sms.v1";
    }
}