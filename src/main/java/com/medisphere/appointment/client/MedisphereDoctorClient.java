package com.medisphere.appointment.client;

import com.medisphere.appointment.client.response.AllDoctorsClientResponse;
import com.medisphere.appointment.client.response.DoctorByIdClientResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "medisphere-doctor-service", fallbackFactory = MedisphereDoctorClientFallbackFactory.class, dismiss404 = true)
public interface MedisphereDoctorClient {

    @GetMapping("/doctor/api/v1/getAllDoctors")
    ResponseEntity<AllDoctorsClientResponse> getAllDoctors();

    @GetMapping("/doctor/api/v1/getDoctorById/{id}")
    ResponseEntity<DoctorByIdClientResponse> getDoctorById(@PathVariable("id") String id);
}

@Component
@Log4j2
class MedisphereDoctorClientFallbackFactory implements FallbackFactory<MedisphereDoctorClient> {
    @Override
    public MedisphereDoctorClient create(Throwable cause) {
        return new MedisphereDoctorClient() {
            @Override
            public ResponseEntity<AllDoctorsClientResponse> getAllDoctors() {
                log.warn("Doctor service is currently unavailable. Please try again later -> getAllDoctors(). Cause: {}", cause.getMessage());
                return ResponseEntity.ok(AllDoctorsClientResponse.builder()
                        .data(java.util.Collections.emptyList())
                        .build());
            }

            @Override
            public ResponseEntity<DoctorByIdClientResponse> getDoctorById(String id) {
                log.warn("Doctor service is currently unavailable. Please try again later -> getDoctorById(). Cause: {}", cause.getMessage());
                return ResponseEntity.ok(DoctorByIdClientResponse.builder().build());
            }
        };
    }
}