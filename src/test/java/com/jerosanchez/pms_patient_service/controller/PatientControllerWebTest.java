package com.jerosanchez.pms_patient_service.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jerosanchez.pms_patient_service.dto.PatientRequestDTO;
import com.jerosanchez.pms_patient_service.dto.PatientResponseDTO;
import com.jerosanchez.pms_patient_service.exception.EmailAlreadyExistsException;
import com.jerosanchez.pms_patient_service.exception.PatientNotFoundException;
import com.jerosanchez.pms_patient_service.mapper.PatientMapper;
import com.jerosanchez.pms_patient_service.model.Patient;
import com.jerosanchez.pms_patient_service.service.PatientService;
import com.jerosanchez.pms_patient_service.test_helpers.DtoTestFactory;
import com.jerosanchez.pms_patient_service.test_helpers.PatientTestFactory;

@WebMvcTest(PatientController.class)
class PatientControllerWebTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService patientService;

    @Autowired
    private ObjectMapper objectMapper;

    // --- Get Patients Tests ---

    @Test
    @SuppressWarnings("null")
    @DisplayName("GET /api/patients returns list of patients")
    void getPatients_returnsList() throws Exception {
        Patient model1 = PatientTestFactory.createRandomPatient();
        Patient model2 = PatientTestFactory.createRandomPatient();
        PatientResponseDTO dto1 = PatientMapper.toDTO(model1);
        PatientResponseDTO dto2 = PatientMapper.toDTO(model2);
        when(patientService.getPatients()).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(dto1.id())))
                .andExpect(jsonPath("$[0].name", is(dto1.name())))
                .andExpect(jsonPath("$[1].id", is(dto2.id())))
                .andExpect(jsonPath("$[1].email", is(dto2.email())));
    }

    @Test
    @SuppressWarnings("null")
    @DisplayName("GET /api/patients returns empty list")
    void getPatients_returnsEmptyList() throws Exception {
        // Arrange
        when(patientService.getPatients()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // --- Create Patient Tests ---

    @Test
    @SuppressWarnings("null")
    @DisplayName("POST /api/patients with valid data creates patient")
    void createPatient_valid_returnsCreated() throws Exception {
        Patient model = com.jerosanchez.pms_patient_service.test_helpers.PatientTestFactory.createRandomPatient();
        PatientRequestDTO request = DtoTestFactory.toRequestDTO(model);
        PatientResponseDTO response = PatientMapper.toDTO(model);
        when(patientService.createPatient(any(PatientRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(response.id())))
                .andExpect(jsonPath("$.name", is(response.name())));
    }

    @Test
    @SuppressWarnings("null")
    @DisplayName("POST /api/patients with invalid data returns 400")
    void createPatient_invalid_returnsBadRequest() throws Exception {
        PatientRequestDTO request = new PatientRequestDTO(); // missing required fields
        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SuppressWarnings("null")
    @DisplayName("POST /api/patients with duplicate email returns 409")
    void createPatient_duplicateEmail_returnsConflict() throws Exception {
        Patient model = com.jerosanchez.pms_patient_service.test_helpers.PatientTestFactory.createRandomPatient();
        PatientRequestDTO request = com.jerosanchez.pms_patient_service.test_helpers.DtoTestFactory.toRequestDTO(model);
        when(patientService.createPatient(any(PatientRequestDTO.class)))
                .thenThrow(new EmailAlreadyExistsException("exists"));

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    // --- Update Patient Tests ---

    @Test
    @SuppressWarnings("null")
    @DisplayName("PUT /api/patients/{id} with valid data updates patient")
    void updatePatient_valid_returnsUpdated() throws Exception {
        Patient model = com.jerosanchez.pms_patient_service.test_helpers.PatientTestFactory.createRandomPatient();
        PatientRequestDTO request = DtoTestFactory.toRequestDTO(model);
        PatientResponseDTO response = PatientMapper.toDTO(model);
        UUID id = model.getId();
        when(patientService.updatePatient(eq(id), any(PatientRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/patients/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(response.id())))
                .andExpect(jsonPath("$.name", is(response.name())));
    }

    @Test
    @SuppressWarnings("null")
    @DisplayName("PUT /api/patients/{id} with invalid data returns 400")
    void updatePatient_invalid_returnsBadRequest() throws Exception {
        UUID id = UUID.randomUUID();
        PatientRequestDTO request = new PatientRequestDTO(); // missing required fields
        mockMvc.perform(put("/api/patients/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SuppressWarnings("null")
    @DisplayName("PUT /api/patients/{id} when not found returns 404")
    void updatePatient_notFound_returnsNotFound() throws Exception {
        Patient model = com.jerosanchez.pms_patient_service.test_helpers.PatientTestFactory.createRandomPatient();
        PatientRequestDTO request = com.jerosanchez.pms_patient_service.test_helpers.DtoTestFactory.toRequestDTO(model);
        UUID id = model.getId();
        when(patientService.updatePatient(eq(id), any(PatientRequestDTO.class)))
                .thenThrow(new PatientNotFoundException("not found"));

        mockMvc.perform(put("/api/patients/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}
