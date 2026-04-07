package com.medisphere.appointment.client;

import com.medisphere.appointment.client.response.DoctorByIdClientResponse;
import com.medisphere.appointment.client.response.PatientByIdClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "medisphere-doctor-service")
public interface MedispherePatientClient {

    @GetMapping("/getPatientById/{id}")
    ResponseEntity<PatientByIdClientResponse> getPatientById(@PathVariable("id") String id);
}
