package com.zooplus.petstore.exception;

import static java.lang.String.format;

public final class ObjectMapperException extends RuntimeException {

    public ObjectMapperException(final String body) {
        super(format("Could not parse request body as String:[%n%s%n]", body));
    }
}
