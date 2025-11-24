package com.jerosanchez.pms_patient_service.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.jerosanchez.pms_patient_service.dto.PatientResponseDTO;
import com.jerosanchez.pms_patient_service.mapper.PatientMapper;
import com.jerosanchez.pms_patient_service.service.PatientService;
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
        PatientResponseDTO dto1 = PatientMapper.toDTO(PatientTestFactory.createRandomPatient());
        PatientResponseDTO dto2 = PatientMapper.toDTO(PatientTestFactory.createRandomPatient());
        List<PatientResponseDTO> dtos = Arrays.asList(dto1, dto2);

        when(patientService.getPatients()).thenReturn(dtos);

        // Act
        ResponseEntity<List<PatientResponseDTO>> response = sut.getPatients();

        // Assert
        verify(patientService, times(1)).getPatients();

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());

        var body = response.getBody();
        assertNotNull(body);
        assertEquals(dtos.size(), body.size());
        assertEquals(dtos, body);
    }

    @Test
    void getPatients_returnsEmptyList() {
        // Arrange
        when(patientService.getPatients()).thenReturn(List.of());

        // Act
        ResponseEntity<List<PatientResponseDTO>> response = sut.getPatients();

        // Assert
        verify(patientService, times(1)).getPatients();

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());

        var body = response.getBody();
        assertNotNull(body);
        assertTrue(body.isEmpty());
    }
}
