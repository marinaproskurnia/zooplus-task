package com.zooplus.petstore.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

/**
 * Represents pet status in the store.
 */
@AllArgsConstructor
public enum Status {

    @JsonProperty("available")
    AVAILABLE,
    @JsonProperty("pending")
    PENDING,
    @JsonProperty("sold")
    SOLD;
}
