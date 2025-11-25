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
import java.util.UUID;

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

    // --- Get Patients Tests ---

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
        assertEquals(2, result.size());
        PatientAssertions.assertEqual(patient1, result.get(0));
        PatientAssertions.assertEqual(patient2, result.get(1));

        verify(patientRepository, times(1)).findAll();
    }

    @Test
    void getPatients_returnsEmptyListWhenNoPatients() {
        // Arrange
        when(patientRepository.findAll()).thenReturn(List.of());

        // Act
        List<PatientResponseDTO> result = sut.getPatients();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(patientRepository, times(1)).findAll();
    }

    // --- Create Patient Tests ---

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
        PatientResponseDTO result = sut.createPatient(request);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDTO, result);

        verify(emailUniquenessPolicy, times(1)).enforce(request.getEmail());
        verify(patientRepository, times(1)).save(ArgumentMatchers.<Patient>any());
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
        assertThrows(EmailAlreadyExistsException.class, () -> sut.createPatient(request));

        verify(emailUniquenessPolicy, times(1)).enforce(request.getEmail());
        verify(patientRepository, times(0)).save(ArgumentMatchers.<Patient>any());
    }

    // --- Update Patient Tests ---

    @Test
    @SuppressWarnings("null")
    void updatePatient_successful() {
        // Arrange
        Patient existingPatient = PatientTestFactory.createRandomPatient();
        PatientRequestDTO updateRequest = toRequestDTO(existingPatient);
        updateRequest.setName("Updated Name");
        updateRequest.setAddress("Updated Address");
        updateRequest.setDateOfBirth(existingPatient.getDateOfBirth().toString());

        when(patientRepository.findById(ArgumentMatchers.<UUID>any()))
                .thenReturn(java.util.Optional.of(existingPatient));
        when(patientRepository.save(existingPatient)).thenReturn(existingPatient);

        // Act
        PatientResponseDTO result = sut.updatePatient(existingPatient.getId(), updateRequest);

        // Assert
        assertEquals("Updated Name", result.name());
        assertEquals("Updated Address", result.address());
        assertEquals(existingPatient.getEmail(), result.email());

        verify(patientRepository, times(1)).findById(ArgumentMatchers.<UUID>any());
        verify(patientRepository, times(1)).save(existingPatient);
    }

    @Test
    @SuppressWarnings("null")
    void updatePatient_enforcesEmailUniquenessWhenEmailChanged() {
        // Arrange
        Patient existingPatient = PatientTestFactory.createRandomPatient();
        PatientRequestDTO updateRequest = toRequestDTO(existingPatient);
        updateRequest.setEmail("newemail@example.com");

        when(patientRepository.findById(ArgumentMatchers.<UUID>any()))
                .thenReturn(java.util.Optional.of(existingPatient));
        when(patientRepository.save(existingPatient)).thenReturn(existingPatient);

        // Act
        sut.updatePatient(existingPatient.getId(), updateRequest);

        // Assert
        verify(emailUniquenessPolicy, times(1)).enforce("newemail@example.com");
        verify(patientRepository, times(1)).save(existingPatient);
    }

    @Test
    @SuppressWarnings("null")
    void updatePatient_doesNotEnforceEmailUniquenessWhenEmailUnchanged() {
        // Arrange
        Patient existingPatient = PatientTestFactory.createRandomPatient();
        PatientRequestDTO updateRequest = toRequestDTO(existingPatient);

        when(patientRepository.findById(ArgumentMatchers.<UUID>any()))
                .thenReturn(java.util.Optional.of(existingPatient));
        when(patientRepository.save(existingPatient)).thenReturn(existingPatient);

        // Act
        sut.updatePatient(existingPatient.getId(), updateRequest);

        // Assert
        verify(emailUniquenessPolicy, times(0)).enforce(updateRequest.getEmail());
        verify(patientRepository, times(1)).save(existingPatient);
    }

    @Test
    @SuppressWarnings("null")
    void updatePatient_throwsWhenPatientNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        PatientRequestDTO updateRequest = new PatientRequestDTO();

        when(patientRepository.findById(ArgumentMatchers.<UUID>any())).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThrows(com.jerosanchez.pms_patient_service.exception.PatientNotFoundException.class,
                () -> sut.updatePatient(id, updateRequest));

        verify(patientRepository, times(1)).findById(ArgumentMatchers.<UUID>any());
        verify(patientRepository, times(0)).save(ArgumentMatchers.<Patient>any());
    }

    @Test
    @SuppressWarnings("null")
    void updatePatient_throwsWhenIdIsNull() {
        // Arrange
        PatientRequestDTO updateRequest = new PatientRequestDTO();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> sut.updatePatient(null, updateRequest));

        verify(patientRepository, times(0)).findById(ArgumentMatchers.<UUID>any());
        verify(patientRepository, times(0)).save(ArgumentMatchers.<Patient>any());
    }
}
