package com.linh.freshfoodbackend.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class JsonBuilder {

    private static ObjectMapper mapper = new ObjectMapper();

    public static <T> String parseString(T object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }

    public static <T> T parObject(String json, Class<T> clazz) throws JsonProcessingException {
        return mapper.readValue(json, clazz);
    }
}
