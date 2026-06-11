package com.praveen.apipnrgateway;

import tools.jackson.databind.ObjectMapper;

public class Utils {
    public static ObjectMapper objectMapper = new ObjectMapper();

    public static <T> String objectToJson(T object) {
        return objectMapper.writeValueAsString(object);
    }

    public static <T> T jsonToObject(String json, Class<T> targetClass) {
        return objectMapper.readValue(json, targetClass);
    }

}
