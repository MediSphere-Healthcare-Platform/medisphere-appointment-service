package com.medisphere.appointment.client;

import com.medisphere.appointment.client.response.DoctorByIdClientResponse;
import com.medisphere.appointment.client.response.PatientByIdClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@FeignClient(name = "medisphere-patient-service", fallbackFactory = MedispherePatientClientFallbackFactory.class, dismiss404 = true)

public interface MedispherePatientClient {

    @GetMapping("/patient/api/v1/getPatientById/{id}")
    ResponseEntity<PatientByIdClientResponse> getPatientById(@PathVariable("id") String id);
}

@Component
@Log4j2
class MedispherePatientClientFallbackFactory implements FallbackFactory<MedispherePatientClient> {
    @Override
    public MedispherePatientClient create(Throwable cause) {
        return new MedispherePatientClient() {
            @Override
            public ResponseEntity<PatientByIdClientResponse> getPatientById(String id) {
                log.warn("Patient service is currently unavailable or patient not found. ID: {}. Cause: {}", id,
                        cause.getMessage());
                return ResponseEntity.ok(PatientByIdClientResponse.builder().build());
            }
        };
    }
}
