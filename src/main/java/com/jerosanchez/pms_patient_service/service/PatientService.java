package com.jerosanchez.pms_patient_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jerosanchez.pms_patient_service.dto.PatientRequestDTO;
import com.jerosanchez.pms_patient_service.dto.PatientResponseDTO;
import com.jerosanchez.pms_patient_service.exception.EmailAlreadyExistsException;
import com.jerosanchez.pms_patient_service.mapper.PatientMapper;
import com.jerosanchez.pms_patient_service.repository.PatientRepository;

@Service
public class PatientService {
    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<PatientResponseDTO> getPatients() {
        var patients = patientRepository.findAll();

        return patients.stream()
                .map(PatientMapper::toDTO)
                .toList();
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        var newPatient = PatientMapper.toModel(patientRequestDTO);

        // An email address must be unique for each patient
        if (patientRepository.existsByEmail(newPatient.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "A patient with the given email already exists: " + newPatient.getEmail());
        }

        var savedPatient = patientRepository.save(newPatient);

        return PatientMapper.toDTO(savedPatient);
    }
}
