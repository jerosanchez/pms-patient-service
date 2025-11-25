package com.jerosanchez.pms_patient_service.policy;

import org.springframework.stereotype.Component;

import com.jerosanchez.pms_patient_service.exception.EmailAlreadyExistsException;
import com.jerosanchez.pms_patient_service.repository.PatientRepository;

@Component
public class EmailUniquenessPolicy implements Policy<String> {
    private final PatientRepository patientRepository;

    public EmailUniquenessPolicy(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public void enforce(String email) {
        if (patientRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(
                    "A patient with the given email already exists: " + email);
        }
    }
}
