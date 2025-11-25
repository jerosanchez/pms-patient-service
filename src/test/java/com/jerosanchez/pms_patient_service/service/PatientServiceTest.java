package com.jerosanchez.pms_patient_service.service;

import static com.jerosanchez.pms_patient_service.test_helpers.DtoTestFactory.toRequestDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.jerosanchez.pms_patient_service.dto.PatientRequestDTO;
import com.jerosanchez.pms_patient_service.dto.PatientResponseDTO;
import com.jerosanchez.pms_patient_service.exception.EmailAlreadyExistsException;
import com.jerosanchez.pms_patient_service.mapper.PatientMapper;
import com.jerosanchez.pms_patient_service.model.Patient;
import com.jerosanchez.pms_patient_service.policy.EmailUniquenessPolicy;
import com.jerosanchez.pms_patient_service.repository.PatientRepository;
import com.jerosanchez.pms_patient_service.test_helpers.PatientAssertions;
import com.jerosanchez.pms_patient_service.test_helpers.PatientTestFactory;

class PatientServiceTest {
    @Mock
    private PatientRepository patientRepository;

    @Mock
    private EmailUniquenessPolicy emailUniquenessPolicy;

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
        PatientAssertions.assertEqual(patient1, result.get(0));
        PatientAssertions.assertEqual(patient2, result.get(1));
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

    @Test
    @SuppressWarnings("null")
    void createPatient_successful() {
        // Arrange
        Patient modelPatient = PatientTestFactory.createRandomPatient();
        PatientRequestDTO request = toRequestDTO(modelPatient);

        Patient savedPatient = modelPatient;
        PatientResponseDTO expectedDTO = PatientMapper.toDTO(savedPatient);

        when(patientRepository.save(ArgumentMatchers.<Patient>any())).thenReturn(savedPatient);

        // Act
        var result = sut.createPatient(request);

        // Assert
        verify(emailUniquenessPolicy, times(1)).enforce(request.getEmail());
        verify(patientRepository, times(1)).save(ArgumentMatchers.<Patient>any());

        assertNotNull(result);
        assertEquals(expectedDTO, result);
    }

    @Test
    @SuppressWarnings("null")
    void createPatient_throwsWhenEmailExists() {
        // Arrange
        Patient modelPatient = PatientTestFactory.createRandomPatient();
        PatientRequestDTO request = toRequestDTO(modelPatient);

        doThrow(new EmailAlreadyExistsException("exists")).when(emailUniquenessPolicy)
                .enforce(request.getEmail());

        // Act & Assert
        assertThrows(EmailAlreadyExistsException.class, () -> {
            sut.createPatient(request);
        });

        verify(emailUniquenessPolicy, times(1)).enforce(request.getEmail());
        verify(patientRepository, times(0)).save(ArgumentMatchers.<Patient>any());
    }

}
