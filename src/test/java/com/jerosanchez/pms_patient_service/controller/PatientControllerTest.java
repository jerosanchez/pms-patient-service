package com.jerosanchez.pms_patient_service.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

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
    void getPatients_returnsEmptyList() {
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

    @Test
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
