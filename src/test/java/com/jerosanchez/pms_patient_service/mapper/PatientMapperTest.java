package com.jerosanchez.pms_patient_service.mapper;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.jerosanchez.pms_patient_service.dto.PatientResponseDTO;
import com.jerosanchez.pms_patient_service.model.Patient;
import com.jerosanchez.pms_patient_service.test_helpers.PatientAssertions;
import com.jerosanchez.pms_patient_service.test_helpers.PatientTestFactory;

class PatientMapperTest {
    @Test
    void toDTO_mapsAllFieldsCorrectly() {
        // Arrange
        Patient model = PatientTestFactory.createRandomPatient();

        // Act
        PatientResponseDTO dto = PatientMapper.toDTO(model);

        // Assert
        assertNotNull(dto);
        PatientAssertions.assertEqual(model, dto);
    }

    @Test
    void toDTO_nullPatientThrowsException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> PatientMapper.toDTO(null));
    }
}
