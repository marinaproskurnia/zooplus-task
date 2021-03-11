package com.zooplus.petstore.databind;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.zooplus.petstore.exception.ObjectMapperException;

public class RequestBodyConverter {

    private final ObjectMapper objectMapper;

    @Inject
    public RequestBodyConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> String convertRequestBodyToString(T body) {
        try {
            return objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException exception) {
            throw new ObjectMapperException(body.toString());
        }
    }
}
