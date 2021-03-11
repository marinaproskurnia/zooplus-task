package com.zooplus.petstore.configs;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
public class RequestResponseLoggingInterceptor implements ClientHttpRequestInterceptor {

    @Override
    @SneakyThrows
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
        logRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        logResponse(response);

        return response;
    }

    @SneakyThrows
    private void logRequest(HttpRequest request, byte[] body) {
        log.info("Request");
        log.info("URI :" + request.getURI());
        log.info("Headers: " + request.getHeaders());
        log.info("Request body: {}", new String(body, UTF_8));
    }

    @SneakyThrows
    private void logResponse(ClientHttpResponse response) {
        log.info("Response");
        logStatus(response.getStatusCode());
        log.info("Headers: " + response.getHeaders());
        log.info("Response body: {}", StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
    }

    private void logStatus(HttpStatus statusCode) {
        log.info(String.format("Status code: [%s][%s]", statusCode.value(), statusCode.getReasonPhrase()));
    }
}
