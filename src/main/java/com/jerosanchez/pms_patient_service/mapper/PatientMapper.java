package com.jerosanchez.pms_patient_service.mapper;

import com.jerosanchez.pms_patient_service.dto.PatientResponseDTO;
import com.jerosanchez.pms_patient_service.model.Patient;

public class PatientMapper {
    public static PatientResponseDTO toDTO(Patient model) {
        return new PatientResponseDTO(
                model.getId().toString(),
                model.getName(),
                model.getEmail(),
                model.getAddress(),
                model.getDateOfBirth().toString());
    }
}