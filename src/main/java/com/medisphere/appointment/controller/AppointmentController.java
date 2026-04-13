package com.medisphere.appointment.controller;

import com.medisphere.appointment.domain.*;
import com.medisphere.appointment.dto.Request.*;
import com.medisphere.appointment.dto.Response.TrackAppointmentStatusResponseDTO;
import com.medisphere.appointment.service.AppointmentService;
import com.medisphere.appointment.util.Utility;
import com.medisphere.appointment.util.EndPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Log4j2
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Validated
public class AppointmentController {

    public final AppointmentService appointmentService;
    public final ModelMapper modelMapper;

    @GetMapping(value = EndPoint.GET_ALL_APPOINTMENTS)
    public ResponseEntity<Object> getAllAppointments() {
        log.info("Received request to get all appointments");
        return appointmentService.getAllAppointments();
    }

    @GetMapping(value = EndPoint.DOCTORS_BY_SPECIALITY)
    public ResponseEntity<Object> getDoctorsBySpeciality(@PathVariable("speciality") String speciality) {
        log.info("Received request to get doctors by speciality: {}", speciality);
        DoctorsBySpecialityRequestDTO requestDTO = DoctorsBySpecialityRequestDTO.builder().speciality(speciality).build();
        return appointmentService.getDoctorsBySpeciality(modelMapper.map(requestDTO, GetDoctorsBySpecialityRequest.class));
    }

    @PostMapping(value = EndPoint.BOOK_APPOINTMENT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> bookAppointment(@Validated @RequestBody BookAppointmentRequestDTO requestDTO) {
        log.info("Received request to book an appointment: {}", Utility.objectToJson(requestDTO));
        return appointmentService.bookAppointment(modelMapper.map(requestDTO, BookAppointmentRequest.class));
    }

    @PutMapping(value = EndPoint.UPDATE_APPOINTMENT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateAppointment(@Validated @RequestBody AppointmentUpdateRequestDTO requestDTO) {
        log.info("Received request to update appointment: {}", Utility.objectToJson(requestDTO));
        return appointmentService.updateAppointment(modelMapper.map(requestDTO, AppointmentUpdateRequest.class));
    }

    @DeleteMapping(value = EndPoint.CANCEL_APPOINTMENT)
    public ResponseEntity<Object> cancelAppointment(@PathVariable("appointmentReferenceId") String appointmentReferenceId) {
        log.info("Received request to cancel appointment: {}", appointmentReferenceId);
        AppointmentCancelRequestDTO requestDTO = AppointmentCancelRequestDTO.builder().appointmentReferenceId(appointmentReferenceId).build();
        return appointmentService.cancelAppointment(modelMapper.map(requestDTO, AppointmentCancelRequest.class));
    }

    @GetMapping(value = EndPoint.TRACK_APPOINTMENT_STATUS)
    public ResponseEntity<Object> trackAppointmentStatus(@PathVariable("appointmentReferenceId") String appointmentReferenceId) {
        log.info("Received request to track appointment status: {}", appointmentReferenceId);
        AppointmentTrackRequestDTO requestDTO = AppointmentTrackRequestDTO.builder().appointmentReferenceId(appointmentReferenceId).build();
        return appointmentService.trackAppointmentStatus(modelMapper.map(requestDTO, AppointmentTrackRequest.class));
    }

    @PutMapping(value = EndPoint.APPOINTMENT_STATUS_CHANGE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> appointmentStatusChange(@Validated @RequestBody AppointmentStatusChangeRequestDTO requestDTO) {
        log.info("Received request to appointment status change: {}", Utility.objectToJson(requestDTO));
        return appointmentService.appointmentStatusChange(modelMapper.map(requestDTO, AppointmentStatusChangeRequest.class));
    }

    @GetMapping(value = EndPoint.ALL_APPOINTMENTS_BY_PATIENT_ID)
    public ResponseEntity<Object> getAllAppointmentsByPatientId(@PathVariable("patientId") String patientId) {
        log.info("Received request to get all appointments by patient id: {}", patientId);
        AppointmentsByPatientIdRequestDTO requestDTO = AppointmentsByPatientIdRequestDTO.builder().patientId(patientId).build();
        return appointmentService.getAllAppointmentsByPatientId(modelMapper.map(requestDTO, AppointmentsByPatientIdRequest.class));
    }

    @GetMapping(value = EndPoint.ALL_APPOINTMENTS_BY_DOCTOR_ID)
    public ResponseEntity<Object> getAllAppointmentsByDoctorId(@PathVariable("doctorId") String doctorId) {
        log.info("Received request to get all appointments by doctor id: {}", doctorId);
        AppointmentsByDoctorIdRequestDTO requestDTO = AppointmentsByDoctorIdRequestDTO.builder().doctorId(doctorId).build();
        return appointmentService.getAllAppointmentsByDoctorId(modelMapper.map(requestDTO, AppointmentsByDoctorIdRequest.class));
    }

}
