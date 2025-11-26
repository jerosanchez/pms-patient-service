package com.jerosanchez.pms_patient_service.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.jerosanchez.pms_patient_service.dto.PatientRequestDTO;
import com.jerosanchez.pms_patient_service.dto.PatientResponseDTO;
import com.jerosanchez.pms_patient_service.exception.EmailAlreadyExistsException;
import com.jerosanchez.pms_patient_service.mapper.PatientMapper;
import com.jerosanchez.pms_patient_service.model.Patient;
import com.jerosanchez.pms_patient_service.service.PatientService;
import com.jerosanchez.pms_patient_service.test_helpers.DtoTestFactory;
import com.jerosanchez.pms_patient_service.test_helpers.PatientAssertions;
import com.jerosanchez.pms_patient_service.test_helpers.PatientTestFactory;

class PatientControllerTest {

    @Mock
    private PatientService patientService;

    @InjectMocks
    private PatientController sut;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- Get Patients Tests ---

    @Test
    void getPatients_returnsListAndOk() {
        // Arrange
        List<PatientResponseDTO> dtos = createRandomPatientResponseDTOList(2);
        when(patientService.getPatients()).thenReturn(dtos);

        // Act
        ResponseEntity<List<PatientResponseDTO>> response = sut.getPatients();

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        List<PatientResponseDTO> body = response.getBody();
        assertNotNull(body);
        assertEquals(dtos.size(), body.size());
        assertEquals(dtos, body);

        verify(patientService, times(1)).getPatients();
    }

    @Test
    void getPatients_returnsEmptyListAndOk() {
        // Arrange
        when(patientService.getPatients()).thenReturn(List.of());

        // Act
        ResponseEntity<List<PatientResponseDTO>> response = sut.getPatients();

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());

        List<PatientResponseDTO> body = response.getBody();
        assertNotNull(body);
        assertTrue(body.isEmpty());

        verify(patientService, times(1)).getPatients();
    }

    // --- Create Patient Tests ---

    void createPatient_returnsCreatedPatientAndOk() {
        // Arrange
        Patient model = PatientTestFactory.createRandomPatient();
        PatientRequestDTO request = DtoTestFactory.toRequestDTO(model);
        PatientResponseDTO responseDTO = PatientMapper.toDTO(model);

        when(patientService.createPatient(request)).thenReturn(responseDTO);

        // Act
        ResponseEntity<PatientResponseDTO> response = sut.createPatient(request);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        PatientAssertions.assertEqual(model, response.getBody());

        verify(patientService, times(1)).createPatient(request);
    }

    @Test
    void createPatient_whenServiceThrowsEmailAlreadyExists_returnsException() {
        // Arrange
        Patient model = PatientTestFactory.createRandomPatient();
        PatientRequestDTO request = DtoTestFactory.toRequestDTO(model);

        when(patientService.createPatient(request))
                .thenThrow(new EmailAlreadyExistsException("exists"));

        // Act & Assert
        assertThrows(EmailAlreadyExistsException.class, () -> sut.createPatient(request));

        verify(patientService, times(1)).createPatient(request);
    }

    // --- Update Patient Tests ---

    @Test
    void updatePatient_returnsUpdatedPatientAndOk() {
        // Arrange
        Patient model = PatientTestFactory.createRandomPatient();

        // No need to update fields, we are only testing the controller layer
        PatientRequestDTO request = DtoTestFactory.toRequestDTO(model);
        PatientResponseDTO responseDTO = PatientMapper.toDTO(model);

        when(patientService.updatePatient(model.getId(), request)).thenReturn(responseDTO);

        // Act
        ResponseEntity<PatientResponseDTO> response = sut.updatePatient(model.getId(), request);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        PatientAssertions.assertEqual(model, response.getBody());

        verify(patientService, times(1)).updatePatient(model.getId(), request);
    }

    // --- Delete Patient Tests ---

    @Test
    void deletePatient_returnsNoContent() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        ResponseEntity<Void> response = sut.deletePatient(id);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCode().value());

        verify(patientService, times(1)).deletePatient(id);
    }

    @Test
    void deletePatient_whenServiceThrowsIllegalArgumentException_returnsException() {
        // Arrange
        UUID id = null;
        doThrow(new IllegalArgumentException("Patient ID cannot be null for update operation.")).when(patientService)
                .deletePatient(id);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> sut.deletePatient(id));

        verify(patientService, times(1)).deletePatient(id);
    }

    // Helper methods

    private List<PatientResponseDTO> createRandomPatientResponseDTOList(int count) {
        List<PatientResponseDTO> dtos = new java.util.ArrayList<>();
        for (int i = 0; i < count; i++) {
            Patient model = PatientTestFactory.createRandomPatient();
            dtos.add(PatientMapper.toDTO(model));
        }
        return dtos;
    }

}
