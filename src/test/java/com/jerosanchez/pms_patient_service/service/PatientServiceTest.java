package com.jerosanchez.pms_patient_service.service;

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

import com.jerosanchez.pms_patient_service.dto.PatientResponseDTO;
import com.jerosanchez.pms_patient_service.model.Patient;
import com.jerosanchez.pms_patient_service.repository.PatientRepository;
import com.jerosanchez.pms_patient_service.test_helpers.PatientTestFactory;

class PatientServiceTest {
    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService sut;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getPatients_returnsMappedDTOs() {
        // Arrange
        Patient patient1 = PatientTestFactory.createRandomPatient();
        Patient patient2 = PatientTestFactory.createRandomPatient();
        List<Patient> patients = Arrays.asList(patient1, patient2);

        when(patientRepository.findAll()).thenReturn(patients);

        // Act
        List<PatientResponseDTO> result = sut.getPatients();

        // Assert
        verify(patientRepository, times(1)).findAll();

        assertEquals(2, result.size());
        assertPatientsEqual(patient1, result.get(0));
        assertPatientsEqual(patient2, result.get(1));
    }

    @Test
    void getPatients_returnsEmptyListWhenNoPatients() {
        // Arrange
        when(patientRepository.findAll()).thenReturn(List.of());

        // Act
        List<PatientResponseDTO> result = sut.getPatients();

        // Assert
        verify(patientRepository, times(1)).findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    private void assertPatientsEqual(Patient expected, PatientResponseDTO actual) {
        assertEquals(expected.getId().toString(), actual.id(), "Patient id");
        assertEquals(expected.getName(), actual.name(), "Patient name");
        assertEquals(expected.getEmail(), actual.email(), "Patient email");
        assertEquals(expected.getAddress(), actual.address(), "Patient address");
        assertEquals(expected.getDateOfBirth().toString(), actual.dateOfBirth(), "Patient dateOfBirth");
        assertEquals(expected.getRegisteredDate().toString(), actual.registeredDate(), "Patient registeredDate");
    }

}
