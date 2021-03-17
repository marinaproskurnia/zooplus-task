package com.zooplus.petstore.integration;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.zooplus.petstore.assertion.ResponseAssertion;
import com.zooplus.petstore.bindings.PetstoreModule;
import com.zooplus.petstore.data.PetDataProvider;
import com.zooplus.petstore.model.Category;
import com.zooplus.petstore.model.Pet;
import com.zooplus.petstore.model.PetUpdateStatus;
import com.zooplus.petstore.service.PetService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static com.zooplus.petstore.model.Status.PENDING;
import static com.zooplus.petstore.model.Status.SOLD;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.util.StringUtils.capitalize;

@DisplayName("Checks everything about your Pets.")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PetTest {

    private static final Pet VALID_PET_DATA = PetDataProvider.getValidPetData();
    @Inject
    private PetService petService;

    public PetTest() {
        Guice.createInjector(new PetstoreModule()).injectMembers(this);
    }

    private static Stream<Arguments> invalidPetStatuses() {
        return Stream.of(
                Arguments.of(SOLD.toString()),
                Arguments.of(capitalize(PENDING.toString().toLowerCase())),
                Arguments.of("invalid")
        );
    }

    private static Stream<Arguments> invalidPetIds() {
        final var randomNumberGenerator = ThreadLocalRandom.current();
        return Stream.of(
                Arguments.of(randomNumberGenerator.nextLong(Integer.MAX_VALUE + 1, Long.MAX_VALUE)),
                Arguments.of(randomNumberGenerator.nextLong(Long.MIN_VALUE, 0))
        );
    }

    @Test
    @DisplayName("Add a new pet to the store")
    @Order(1)
    void checkNewPetAddedToStore() {
        final var response = petService.addNewPetToStore(VALID_PET_DATA);
        final var petAdded = ResponseAssertion.assertThat(response)
                .isStatusOk()
                .hasBody()
                .getBody();
        assertThat(petAdded)
                .as(format("Check pet '%s' was added successfully with ID = '%s'", VALID_PET_DATA.getName(),
                        VALID_PET_DATA.getId()))
                .isEqualTo(VALID_PET_DATA);
    }

    @ParameterizedTest(name = "When pet ID has invalid value [{0}], the record would not be added")
    @MethodSource("invalidPetIds")
    void checkPetWithIdExceedingLimitNotAdded(long invalidPetId) {
        final var response = petService.addNewPetToStore(
                PetDataProvider.getPetWithInvalidId(invalidPetId));
        ResponseAssertion.assertThat(response)
                .describedAs("ID exceeded max allowed value")
                .isRecordNotCreated();
    }

    @Test
    @DisplayName("Pet could be found by its ID")
    @Order(2)
    void checkPetCouldBeFoundByItsId() {
        final var petId = VALID_PET_DATA.getId();
        final var response = petService.findPetById(petId);
        final var foundPet = ResponseAssertion.assertThat(response)
                .isStatusOk()
                .hasBody()
                .getBody();

        assertThat(foundPet)
                .as(format("Compare pet, found by ID=%s, with expectation", petId))
                .isEqualTo(VALID_PET_DATA);
    }

    @Test
    @DisplayName("Pets could be found by their Status")
    @Order(2)
    void checkPetsCouldBeFoundByTheirStatus() {
        final var minNumberOfPetsFound = 1;
        final var response = petService.findPetsByStatus(VALID_PET_DATA.getStatus());
        final var foundPets = ResponseAssertion.assertThat(response)
                .isStatusOk()
                .hasBody()
                .getBody();

        assertThat(foundPets.length)
                .as(format("Check number of pets, found by status, is more than %s", minNumberOfPetsFound))
                .isGreaterThanOrEqualTo(minNumberOfPetsFound);
    }

    @Test
    @DisplayName("No pet is found when invalid ID was provided")
    void checkNoPetFoundByInvalidId() {
        final var invalidPetId = Long.MAX_VALUE;
        final var response = petService.findPetById(invalidPetId);
        ResponseAssertion.assertThat(response)
                .as("Invalid ID supplied")
                .isBadRequest();
    }

    @ParameterizedTest(name = "No pets would be found, when invalid status [{0}] was provided")
    @MethodSource("invalidPetStatuses")
    void checkNoPetsFoundWhenInvalidStatusProvided(String invalidStatus) {
        final var response = petService.findPetsByStatus(invalidStatus);
        ResponseAssertion.assertThat(response)
                .describedAs("Invalid status was provided")
                .isBadRequest();
    }

    @Test
    @Order(2)
    @DisplayName("Check pet ID is unique. New record with the same ID would be rejected.")
    void checkPetIdIsUniqueAndNewPetWithSameIdNotAdded() {
        final var response = petService.addNewPetToStore(VALID_PET_DATA);
        ResponseAssertion.assertThat(response)
                .as("Invalid input")
                .isRecordNotCreated();
    }

    @Test
    @Order(2)
    @DisplayName("Update existing pet")
    void checkPetCouldBeUpdated() {
        final var petToBeUpdated = Pet.builder()
                .id(VALID_PET_DATA.getId())
                .category(new Category(VALID_PET_DATA.getCategory().getId(), "Alligator"))
                .photoUrls(VALID_PET_DATA.getPhotoUrls())
                .tags(VALID_PET_DATA.getTags())
                .status(PENDING)
                .build();
        final var response = petService.updateExistingPet(petToBeUpdated);
        final var updatedPet = ResponseAssertion.assertThat(response)
                .isStatusOk()
                .hasBody()
                .getBody();
        assertThat(updatedPet)
                .as(format("Check pet with ID = '%s' has been updated successfully ", petToBeUpdated.getId()))
                .isEqualTo(petToBeUpdated);
    }

    @Test
    @Order(2)
    @DisplayName("Existing pet remains without changes, when invalid ID was provided during it's update")
    void checkExistingPetRemainsWithoutChangesWhenInvalidIdWasProvided() {
        final var petToBeUpdatedViaInvalidId = Pet.builder()
                .id(Long.MIN_VALUE)
                .photoUrls(Lists.emptyList())
                .tags(Lists.emptyList())
                .build();
        final var response = petService.updateExistingPet(petToBeUpdatedViaInvalidId);
        ResponseAssertion.assertThat(response)
                .as("Invalid ID supplied")
                .isBadRequest();
    }

    @Test
    @Order(2)
    @DisplayName("Existing pet remains without changes, when update information didn't pass validation")
    void checkExistingPetRemainsWithoutChangesWhenUpdateNotPassedValidation() {
        final var petWithInvalidStatus = Pet.builder()
                .id(VALID_PET_DATA.getId())
                .photoUrls(Lists.emptyList())
                .tags(Lists.emptyList())
                .status(SOLD)
                .build();
        final var response = petService.updateExistingPet(petWithInvalidStatus);
        ResponseAssertion.assertThat(response)
                .as("Validation exception")
                .isRecordNotCreated();
    }

    @Test
    @DisplayName("No pet is removed, when invalid ID was provided")
    void checkNoPetIsRemovedWhenInvalidIdWasProvided() {
        final var invalidPetId = Long.MAX_VALUE;
        final var response = petService.deletePet(invalidPetId);
        ResponseAssertion.assertThat(response)
                .as("Pet not found")
                .isRecordNotFound();
    }

    @Test
    @Order(3)
    @DisplayName("Pet could be removed by it's ID")
    void checkPetCouldBeRemovedById() {
        final var response = petService.deletePet(VALID_PET_DATA.getId());
        ResponseAssertion.assertThat(response)
                .as("Deletes a pet")
                .isStatusOk()
                .hasBody()
                .getBody();
    }

    @Test
    @Order(4)
    @DisplayName("When pet was already removed, it's update fails")
    void checkWhenPetWasRemovedItsUpdateFails() {
        final var response = petService.updateExistingPet(VALID_PET_DATA);
        ResponseAssertion.assertThat(response)
                .as("Pet not found")
                .isRecordNotFound();
    }

    @Test
    @Order(2)
    @DisplayName("Update pet name and status via it's ID")
    void checkPetNameAndStatusCouldBeUpdatedViaId() {
        final var id = VALID_PET_DATA.getId();
        final var response = petService.updatePetWithFormData(id,
                VALID_PET_DATA.getName() + "_2",
                "sold");
        final var petUpdatedReport = ResponseAssertion.assertThat(response)
                .as("Pet name and status changed successfully")
                .isStatusOk()
                .hasBody()
                .getBody();
        final var expectedUpdateReport = PetUpdateStatus.builder()
                .code(OK.value())
                .message(String.valueOf(id))
                .build();
        assertThat(petUpdatedReport)
                .as(format("Check pet with ID '%s' was updated successfully ", id))
                .usingRecursiveComparison()
                .ignoringFields("type")
                .isEqualTo(expectedUpdateReport);
    }

    @Test
    @Order(2)
    @DisplayName("Upload Pet image")
    void checkPetImageCouldBeUploaded() {
        final var id = VALID_PET_DATA.getId();
        final var imageFile = "pet.png";
        final var response = petService.uploadImageById(id, imageFile);
        final var petUpdatedReport = ResponseAssertion.assertThat(response)
                .as("Pet image uploaded successfully")
                .isStatusOk()
                .hasBody()
                .getBody();
        final var expectedUpdateReport = PetUpdateStatus.builder()
                .code(OK.value())
                .message(String.valueOf(id))
                .build();
        assertThat(petUpdatedReport)
                .as(format("Check pet with ID '%s' was updated successfully ", id))
                .usingRecursiveComparison()
                .ignoringFields("type")
                .isEqualTo(expectedUpdateReport);
    }
}
