package com.jerosanchez.pms_patient_service.policy;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.jerosanchez.pms_patient_service.exception.EmailAlreadyExistsException;
import com.jerosanchez.pms_patient_service.repository.PatientRepository;
import com.jerosanchez.pms_patient_service.test_helpers.PatientTestFactory;

class EmailUniquenessPolicyTest {
    @Mock
    private PatientRepository patientRepository;

    private EmailUniquenessPolicy emailUniquenessPolicy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        emailUniquenessPolicy = new EmailUniquenessPolicy(patientRepository);
    }

    @Test
    void enforce_whenEmailDoesNotExist_doesNotThrow() {
        // Arrange
        String email = PatientTestFactory.createRandomPatient().getEmail();
        when(patientRepository.existsByEmail(email)).thenReturn(false);

        // Act
        emailUniquenessPolicy.enforce(email);

        // Assert
        verify(patientRepository).existsByEmail(email);
    }

    @Test
    void enforce_whenEmailExists_throwsEmailAlreadyExistsException() {
        // Arrange
        String email = PatientTestFactory.createRandomPatient().getEmail();
        when(patientRepository.existsByEmail(email)).thenReturn(true);

        // Act & Assert
        assertThrows(EmailAlreadyExistsException.class, () -> emailUniquenessPolicy.enforce(email));

        verify(patientRepository).existsByEmail(email);
    }

    @Test
    void enforce_whenEmailIsNull_doesNotThrow() {
        // Arrange
        when(patientRepository.existsByEmail(null)).thenReturn(false);

        // Act
        emailUniquenessPolicy.enforce(null);

        // Assert
        verify(patientRepository).existsByEmail(null);
    }
}
