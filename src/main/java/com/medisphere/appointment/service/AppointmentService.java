package com.medisphere.appointment.service;

import com.medisphere.appointment.domain.*;
import org.springframework.http.ResponseEntity;

public interface AppointmentService {

    ResponseEntity<Object> getAllAppointments();

    ResponseEntity<Object> getDoctorsBySpeciality(GetDoctorsBySpecialityRequest request);

    ResponseEntity<Object> bookAppointment(BookAppointmentRequest request);

    ResponseEntity<Object> updateAppointment(AppointmentUpdateRequest request);

    ResponseEntity<Object> cancelAppointment(AppointmentCancelRequest request);

    ResponseEntity<Object> trackAppointmentStatus(AppointmentTrackRequest request);

}
