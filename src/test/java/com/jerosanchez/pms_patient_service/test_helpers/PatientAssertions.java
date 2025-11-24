package com.jerosanchez.pms_patient_service.test_helpers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jerosanchez.pms_patient_service.dto.PatientResponseDTO;
import com.jerosanchez.pms_patient_service.model.Patient;

public class PatientAssertions {
    public static void assertEqual(Patient model, PatientResponseDTO dto) {
        assertEquals(model.getId().toString(), dto.id(), "Patient id");
        assertEquals(model.getName(), dto.name(), "Patient name");
        assertEquals(model.getEmail(), dto.email(), "Patient email");
        assertEquals(model.getAddress(), dto.address(), "Patient address");
        assertEquals(model.getDateOfBirth().toString(), dto.dateOfBirth(), "Patient dateOfBirth");
    }
}
