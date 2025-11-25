package com.jerosanchez.pms_patient_service.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.jerosanchez.pms_patient_service.dto.PatientRequestDTO;
import com.jerosanchez.pms_patient_service.dto.PatientResponseDTO;
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
}
