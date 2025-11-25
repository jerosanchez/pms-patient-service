package com.jerosanchez.pms_patient_service.policy;

public interface Policy<T> {
    /**
     * Enforces the policy for the given value. Throws an exception if the policy is violated.
     * @param value the value to check
     */
    void enforce(T value);
}
