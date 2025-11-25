package com.jerosanchez.pms_patient_service.test_helpers;

import com.jerosanchez.pms_patient_service.dto.PatientRequestDTO;
import com.jerosanchez.pms_patient_service.model.Patient;

public class DtoTestFactory {
    private DtoTestFactory() {}

    public static PatientRequestDTO toRequestDTO(Patient patient) {
        PatientRequestDTO dto = new PatientRequestDTO();
        dto.setName(patient.getName());
        dto.setEmail(patient.getEmail());
        dto.setAddress(patient.getAddress());
        dto.setDateOfBirth(patient.getDateOfBirth().toString());
        dto.setRegisteredDate(patient.getRegisteredDate().toString());
        return dto;
    }
}
