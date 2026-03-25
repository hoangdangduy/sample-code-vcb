package com.vcb.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class StringUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String convertString(Object args) {
        try {
            return objectMapper.writeValueAsString(args);
        } catch (JsonProcessingException e) {
            log.warn("Failed to convert argument to JSON string: {}", e.getMessage());
            return null;
        }
    }
}
