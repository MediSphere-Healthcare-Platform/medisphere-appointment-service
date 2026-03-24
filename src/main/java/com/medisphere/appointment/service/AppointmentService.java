package com.medisphere.appointment.service;

import com.medisphere.appointment.domain.GetDoctorsBySpecialityRequest;
import org.springframework.http.ResponseEntity;

public interface AppointmentService {
    ResponseEntity<Object> getDoctorsBySpeciality(GetDoctorsBySpecialityRequest request);

}
