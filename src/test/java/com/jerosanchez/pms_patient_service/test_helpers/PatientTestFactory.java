package com.jerosanchez.pms_patient_service.test_helpers;

import java.util.UUID;

import com.jerosanchez.pms_patient_service.model.Patient;

public class PatientTestFactory {
    public static Patient createRandomPatient() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());
        patient.setName(RandomTestData.randomName());
        patient.setEmail(RandomTestData.randomEmail());
        patient.setAddress(RandomTestData.randomAddress());
        patient.setDateOfBirth(RandomTestData.randomDate(1950, 2010));
        patient.setRegisteredDate(RandomTestData.randomDate(2015, 2025));
        return patient;
    }
}
