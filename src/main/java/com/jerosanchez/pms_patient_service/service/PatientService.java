package com.jerosanchez.pms_patient_service.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.jerosanchez.pms_patient_service.dto.PatientRequestDTO;
import com.jerosanchez.pms_patient_service.dto.PatientResponseDTO;
import com.jerosanchez.pms_patient_service.exception.PatientNotFoundException;
import com.jerosanchez.pms_patient_service.mapper.PatientMapper;
import com.jerosanchez.pms_patient_service.policy.EmailUniquenessPolicy;
import com.jerosanchez.pms_patient_service.repository.PatientRepository;

@Service
public class PatientService {
    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);
    private final PatientRepository patientRepository;
    private final EmailUniquenessPolicy emailUniquenessPolicy;

    public PatientService(PatientRepository patientRepository, EmailUniquenessPolicy emailUniquenessPolicy) {
        this.patientRepository = patientRepository;
        this.emailUniquenessPolicy = emailUniquenessPolicy;
    }

    public List<PatientResponseDTO> getPatients() {
        var patients = patientRepository.findAll();

        return patients.stream()
                .map(PatientMapper::toDTO)
                .toList();
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        var newPatient = PatientMapper.toModel(patientRequestDTO);

        emailUniquenessPolicy.enforce(newPatient.getEmail());

        var savedPatient = patientRepository.save(newPatient);

        logger.info("Patient created successfully: id={}, email={}",
                savedPatient.getId(), savedPatient.getEmail());
        return PatientMapper.toDTO(savedPatient);
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {
        if (id == null) {
            throw new IllegalArgumentException("Patient ID cannot be null for update operation.");
        }

        var existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + id));

        if (!existingPatient.getEmail().equals(patientRequestDTO.getEmail())) {
            emailUniquenessPolicy.enforce(patientRequestDTO.getEmail());
        }

        existingPatient.setName(patientRequestDTO.getName());
        existingPatient.setEmail(patientRequestDTO.getEmail());
        existingPatient.setAddress(patientRequestDTO.getAddress());
        existingPatient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));

        var updatedPatient = patientRepository.save(existingPatient);

        logger.info("Patient updated successfully: id={}, email={}, address={}, dateOfBirth={}",
                updatedPatient.getId(), updatedPatient.getEmail(), updatedPatient.getAddress(),
                updatedPatient.getDateOfBirth());
        return PatientMapper.toDTO(updatedPatient);
    }
}
