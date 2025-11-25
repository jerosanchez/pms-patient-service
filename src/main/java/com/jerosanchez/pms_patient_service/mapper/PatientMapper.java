package com.jerosanchez.pms_patient_service.mapper;

import java.time.LocalDate;

import com.jerosanchez.pms_patient_service.dto.PatientRequestDTO;
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

    public static Patient toModel(PatientRequestDTO dto) {
        Patient patient = new Patient();

        patient.setName(dto.getName());
        patient.setEmail(dto.getEmail());
        patient.setAddress(dto.getAddress());
        patient.setDateOfBirth(LocalDate.parse(dto.getDateOfBirth()));
        patient.setRegisteredDate(LocalDate.parse(dto.getRegisteredDate()));

        return patient;
    }
}