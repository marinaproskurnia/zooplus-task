package com.zooplus.petstore.assertion;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.springframework.http.HttpStatus.OK;

public class ResponseAssertion<T> extends AbstractAssert<ResponseAssertion<T>, ResponseEntity<T>> {

    private ResponseAssertion(final ResponseEntity<T> responseEntity) {
        super(responseEntity, ResponseAssertion.class);
    }

    public static <T> ResponseAssertion<T> assertThat(ResponseEntity<T> responseEntity) {
        return new ResponseAssertion<>(responseEntity);
    }

    public ResponseAssertion<T> isStatusOk() {
        isNotNull();
        Assertions.assertThat(actual.getStatusCode()).isEqualTo(OK);
        return this;
    }

    public ResponseAssertion<T> isBadRequest() {
        isNotNull();
        Assertions.assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        return this;
    }

    public ResponseAssertion<T> isRecordNotFound() {
        isNotNull();
        Assertions.assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        return this;
    }

    public ResponseAssertion<T> isRecordNotCreated() {
        isNotNull();
        Assertions.assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        return this;
    }

    public ResponseAssertion<T> hasBody() {
        isNotNull();
        Assertions.assertThat(actual.getBody()).isNotNull();
        return this;
    }

    public T getBody() {
        return actual.getBody();
    }
}
