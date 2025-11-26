package com.jerosanchez.pms_patient_service.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.jerosanchez.pms_patient_service.dto.PatientRequestDTO;
import com.jerosanchez.pms_patient_service.dto.PatientResponseDTO;
import com.jerosanchez.pms_patient_service.test_helpers.DtoTestFactory;
import com.jerosanchez.pms_patient_service.test_helpers.PatientTestFactory;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PatientITCase {
        @Autowired
        private TestRestTemplate restTemplate;

        @Test
        @SuppressWarnings("null")
        @DisplayName("Happy path: create, get, update, delete patient")
        void patientLifecycle_happyPath() {
                // Arrange
                // Create a random patient model and request DTO
                PatientRequestDTO createRequest = DtoTestFactory.toRequestDTO(PatientTestFactory.createRandomPatient());

                // Act
                // 1. Create patient
                ResponseEntity<PatientResponseDTO> createResp = restTemplate.postForEntity(
                                "/api/patients", createRequest, PatientResponseDTO.class);

                // Assert
                assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.OK);
                PatientResponseDTO created = createResp.getBody();
                assertThat(created).isNotNull();
                assertThat(created.id()).isNotNull();
                assertThat(created.name()).isEqualTo(createRequest.getName());

                // Act
                // 2. Get all patients
                ResponseEntity<PatientResponseDTO[]> getResp = restTemplate.getForEntity(
                                "/api/patients", PatientResponseDTO[].class);

                // Assert
                assertThat(getResp.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(Arrays.stream(getResp.getBody()).anyMatch(p -> p.id().equals(created.id()))).isTrue();

                // Act
                // 3. Update patient
                String updatedName = created.name() + " Updated";
                createRequest.setName(updatedName);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<PatientRequestDTO> updateReq = new HttpEntity<>(createRequest, headers);
                ResponseEntity<PatientResponseDTO> updateResp = restTemplate.exchange(
                                "/api/patients/" + created.id(), HttpMethod.PUT, updateReq, PatientResponseDTO.class);

                // Assert
                assertThat(updateResp.getStatusCode()).isEqualTo(HttpStatus.OK);
                PatientResponseDTO updated = updateResp.getBody();
                assertThat(updated).isNotNull();
                assertThat(updated.name()).isEqualTo(updatedName);

                // Act
                // 4. Delete patient
                restTemplate.delete("/api/patients/" + created.id());

                // Assert
                // 5. Confirm deletion
                ResponseEntity<PatientResponseDTO[]> afterDelete = restTemplate.getForEntity(
                                "/api/patients", PatientResponseDTO[].class);
                assertThat(Arrays.stream(afterDelete.getBody()).noneMatch(p -> p.id().equals(created.id()))).isTrue();
        }

        @Test
        @DisplayName("Sad path: creating patient with duplicate email returns 409 Conflict")
        void createPatient_duplicateEmail_returnsConflict() {
                // Arrange
                String email = "integration-unique@example.com";
                PatientRequestDTO firstRequest = DtoTestFactory.toRequestDTO(PatientTestFactory.createRandomPatient());
                firstRequest.setEmail(email);
                PatientRequestDTO secondRequest = DtoTestFactory.toRequestDTO(PatientTestFactory.createRandomPatient());
                secondRequest.setEmail(email);

                // Act
                ResponseEntity<PatientResponseDTO> firstResp = restTemplate.postForEntity(
                                "/api/patients", firstRequest, PatientResponseDTO.class);
                ResponseEntity<String> secondResp = restTemplate.postForEntity(
                                "/api/patients", secondRequest, String.class);

                // Assert
                assertThat(firstResp.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(secondResp.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                assertThat(secondResp.getBody()).containsIgnoringCase("email");
        }

        @Test
        @DisplayName("Sad path: creating patient with invalid data returns 400 Bad Request")
        void createPatient_invalidData_returnsBadRequest() {
                // Arrange
                PatientRequestDTO invalidRequest = new PatientRequestDTO();
                // All fields null/blank

                // Act
                ResponseEntity<String> response = restTemplate.postForEntity(
                                "/api/patients", invalidRequest, String.class);

                // Assert
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                assertThat(response.getBody()).containsIgnoringCase("name");
                assertThat(response.getBody()).containsIgnoringCase("email");
        }
}
