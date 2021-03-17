package com.zooplus.petstore.service;

import com.google.inject.Inject;
import com.zooplus.petstore.databind.RequestBodyConverter;
import com.zooplus.petstore.model.Pet;
import com.zooplus.petstore.model.PetUpdateStatus;
import com.zooplus.petstore.model.Status;
import io.qameta.allure.Step;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static com.zooplus.petstore.configs.PetstoreConfigs.PLATFORM_CONFIG;
import static java.lang.String.valueOf;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.MediaType.*;

public class PetService {

    private static final String PET_ENDPOINT = PLATFORM_CONFIG.petEndpoint();

    private final TestRestTemplate testRestTemplate;
    private final RequestBodyConverter requestBodyConverter;

    @Inject
    public PetService(final TestRestTemplate testRestTemplate, final RequestBodyConverter requestBodyConverter) {
        this.testRestTemplate = testRestTemplate;
        this.requestBodyConverter = requestBodyConverter;
    }

    @Step("Returns a single pet by ID")
    public ResponseEntity<Pet> findPetById(final long value) {
        final var uri = UriComponentsBuilder.fromUriString(PET_ENDPOINT)
                .pathSegment(valueOf(value))
                .build().toUri();
        return testRestTemplate.getForEntity(uri, Pet.class);
    }

    @Step("Finds Pets by VALID Status")
    public ResponseEntity<Pet[]> findPetsByStatus(final Status status) {
        final var uri = getUriWithStatus(status.toString().toLowerCase());
        return testRestTemplate.getForEntity(uri, Pet[].class);
    }

    @Step("Finds Pets by ANY Status")
    public ResponseEntity<Pet[]> findPetsByStatus(final String status) {
        final var uri = getUriWithStatus(status);
        return testRestTemplate.getForEntity(uri, Pet[].class);
    }

    @Step("Add a new pet to the store")
    public ResponseEntity<Pet> addNewPetToStore(final Pet pet) {
        return testRestTemplate.postForEntity(URI.create(PET_ENDPOINT), getRequestInJsonFormat(pet), Pet.class);
    }

    @Step("Update an existing pet")
    public ResponseEntity<Pet> updateExistingPet(final Pet pet) {
        return testRestTemplate.exchange(URI.create(PET_ENDPOINT), PUT, getRequestInJsonFormat(pet), Pet.class);
    }

    @Step("Deletes a pet")
    public ResponseEntity<PetUpdateStatus> deletePet(final long petId) {
        final var uri = UriComponentsBuilder.fromUriString(PET_ENDPOINT)
                .pathSegment(valueOf(petId))
                .build().toUri();
        final var headers = new HttpHeaders();
        headers.set(ACCEPT, APPLICATION_JSON_VALUE);
        return testRestTemplate.exchange(uri, DELETE, new HttpEntity<>(headers), PetUpdateStatus.class);
    }

    @Step("Updates a pet in the store with form data")
    public ResponseEntity<PetUpdateStatus> updatePetWithFormData(final long petId, final String name,
                                                                 final String status) {
        final var uri = UriComponentsBuilder.fromUriString(PET_ENDPOINT)
                .pathSegment(valueOf(petId))
                .build().toUri();
        final var headers = getHttpHeaders(APPLICATION_FORM_URLENCODED);
        final var updatedParams = new LinkedMultiValueMap<>();
        updatedParams.add("name", name);
        updatedParams.add("status", status);
        final var request = new HttpEntity<>(updatedParams, headers);
        return testRestTemplate.postForEntity(uri, request, PetUpdateStatus.class);
    }

    @Step("Uploads an image by Pet's ID")
    public ResponseEntity<PetUpdateStatus> uploadImageById(final long petId, final String fileName) {
        final var uri = UriComponentsBuilder.fromUriString(PET_ENDPOINT)
                .pathSegment(valueOf(petId))
                .pathSegment("uploadImage")
                .build().toUri();
        final var headers = getHttpHeaders(MULTIPART_FORM_DATA);
        final var body = new LinkedMultiValueMap<>();
        body.add("file", fileName);
        final var request = new HttpEntity<>(body, headers);
        return testRestTemplate.postForEntity(uri, request, PetUpdateStatus.class);
    }

    private HttpHeaders getHttpHeaders(final MediaType applicationFormUrlencoded) {
        final var headers = new HttpHeaders();
        headers.set(ACCEPT, APPLICATION_JSON_VALUE);
        headers.setContentType(applicationFormUrlencoded);
        return headers;
    }

    private HttpEntity<String> getRequestInJsonFormat(final Pet body) {
        final var requestBody = requestBodyConverter.convertRequestBodyToString(body);
        final var headers = getHttpHeaders(APPLICATION_JSON);
        return new HttpEntity<>(requestBody, headers);
    }

    private URI getUriWithStatus(final String value) {
        return UriComponentsBuilder.fromUriString(PET_ENDPOINT)
                .pathSegment("findByStatus")
                .queryParam("status", value)
                .build().toUri();
    }
}
