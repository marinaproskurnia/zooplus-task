package com.zooplus.petstore.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetUpdateStatus {
    int code;
    String type;
    String message;
}
