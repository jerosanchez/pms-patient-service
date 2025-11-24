package com.jerosanchez.pms_patient_service.dto;

public record PatientResponseDTO(
        String id,
        String name,
        String email,
        String address,
        String dateOfBirth) {
}
