package com.zooplus.petstore.bindings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.zooplus.petstore.configs.RequestResponseLoggingInterceptor;
import io.qameta.allure.springweb.AllureRestTemplate;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.Collections;

import static com.zooplus.petstore.configs.PetstoreConfigs.PLATFORM_CONFIG;

public final class PetstoreModule extends AbstractModule {

    @Provides
    public TestRestTemplate getTestRestTemplate() {
        final var messageConverters = new ArrayList<HttpMessageConverter<?>>();
        final var converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);
        return new TestRestTemplate(restTemplateBuilder()
                .rootUri(PLATFORM_CONFIG.petstoreBaseUrl())
                .messageConverters(messageConverters)
        );
    }

    @Provides
    ObjectMapper jacksonObjectMapper() {
        return Jackson2ObjectMapperBuilder.json().build();
    }

    private RestTemplateBuilder restTemplateBuilder() {
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        simpleClientHttpRequestFactory.setOutputStreaming(false);
        return new RestTemplateBuilder()
                .requestFactory(() -> new BufferingClientHttpRequestFactory(simpleClientHttpRequestFactory))
                .additionalInterceptors(
                        new RequestResponseLoggingInterceptor(),
                        new AllureRestTemplate());
    }
}
