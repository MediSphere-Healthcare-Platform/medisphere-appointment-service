package com.medisphere.appointment.service.impl;

import com.medisphere.appointment.domain.AppointmentUpdateRequest;
import com.medisphere.appointment.domain.BookAppointmentRequest;
import com.medisphere.appointment.domain.GetDoctorsBySpecialityRequest;
import com.medisphere.appointment.dto.Response.AppointmentUpdateResponseDTO;
import com.medisphere.appointment.dto.Response.BookAppointmentResponseDTO;
import com.medisphere.appointment.dto.Response.DoctorDetailDTO;
import com.medisphere.appointment.dto.Response.GetDoctorsBySpecialityResponseDTO;
import com.medisphere.appointment.entity.DoctorEntity;
import com.medisphere.appointment.entity.MedisphereAppointmentEntity;
import com.medisphere.appointment.entity.MedispherePatientEntity;
import com.medisphere.appointment.repository.AppointmentRepository;
import com.medisphere.appointment.repository.DoctorRepository;
import com.medisphere.appointment.repository.PatientRepository;
import com.medisphere.appointment.service.AppointmentService;
import com.medisphere.appointment.service.ResponseGenerator;
import com.medisphere.appointment.util.MessageConstant;
import com.medisphere.appointment.util.ResponseCode;
import com.medisphere.appointment.util.enums.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class AppointmentServiceImpl implements AppointmentService {

    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final ResponseGenerator responseGenerator;
    private final AppointmentRepository appointmentRepository;
    private final ModelMapper modelMapper;

    @Override
    public ResponseEntity<Object> getDoctorsBySpeciality(GetDoctorsBySpecialityRequest request) {
        try {
            log.debug("Get Doctors By Speciality Called.");

            List<DoctorEntity> docEntities = doctorRepository.findByStatusAndSpecialty(Status.active.name(), request.getSpeciality());
            log.debug("Doctors Retrieved: {}", docEntities.size());

            if (docEntities.isEmpty()) {
                log.warn("No Doctors For The Given Speciality: {}", request.getSpeciality());
                return responseGenerator.generateResponse(ResponseCode.DOCTORS_NOT_FOUND, MessageConstant.DOCTORS_NOT_FOUND, null);
            }

            // Map Entity to DTO (to avoid exposing sensitive data like NIC and Licence)
            List<DoctorDetailDTO> doctorDetails = docEntities.stream()
                    .map(entity -> modelMapper.map(entity, DoctorDetailDTO.class))
                    .collect(Collectors.toList());

            GetDoctorsBySpecialityResponseDTO getDoctorsBySpecialityResponseDTO = GetDoctorsBySpecialityResponseDTO.builder()
                    .speciality(request.getSpeciality())
                    .data(doctorDetails)
                    .build();

            log.debug("Doctors Retrieval Success.");
            return responseGenerator.generateResponse(ResponseCode.APPOINTMENT_OPERATION_SUCCESS, MessageConstant.DOCTORS_RETRIEVAL_SUCCESS, getDoctorsBySpecialityResponseDTO);

        } catch (Exception e) {
            log.error("Error Occurred: ", e);
            return responseGenerator.generateResponse(ResponseCode.APPOINTMENT_OPERATION_FAILED, MessageConstant.APPOINTMENT_OPERATION_FAILED, null);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Object> bookAppointment(BookAppointmentRequest request) {
        try {
            log.debug("Book Appointment Called.");

            // Validate Active Patient Existence
            MedispherePatientEntity patientEntity = patientRepository.findByIdAndStatus(request.getPatientId(), Status.active.name());
            if (patientEntity == null) {
                log.warn("Patient not found for ID: {}.", request.getPatientId());
                return responseGenerator.generateResponse(ResponseCode.PATIENTS_NOT_FOUND, MessageConstant.PATIENTS_NOT_FOUND, null);
            }

            // Validate Appointment Date (Must be today or in the future)
            if (request.getAppointmentDate().isBefore(LocalDate.now())) {
                log.warn("Booking failed: Date {} is in the past.", request.getAppointmentDate());
                return responseGenerator.generateResponse(ResponseCode.PAST_DATE_ERROR, MessageConstant.PAST_DATE_ERROR, null);
            }

            // Validate Doctor Existence and Status
            DoctorEntity doctorEntity = doctorRepository.findDoctorByDoctorIdAndSpecialty(request.getDoctorId(), request.getSpecialty());
            if (doctorEntity == null) {
                log.warn("Booking failed: Doctor ID {} not found for specialty {}.", request.getDoctorId(), request.getSpecialty());
                return responseGenerator.generateResponse(ResponseCode.DOCTORS_NOT_FOUND, MessageConstant.DOCTORS_NOT_FOUND, null);
            }

            if (!Status.active.name().equalsIgnoreCase(doctorEntity.getStatus())) {
                log.warn("Booking failed: Doctor ID {} is not active: {}", request.getDoctorId(), doctorEntity.getStatus());
                return responseGenerator.generateResponse(ResponseCode.DOCTOR_NOT_ACTIVE, MessageConstant.DOCTOR_NOT_ACTIVE, null);
            }

            // Check for Duplicate Booking (Same patient, doctor, date, and time)
            if (appointmentRepository.findByPatientAndDoctorAndAppointmentDateAndAppointmentTime(patientEntity.getId(), doctorEntity.getId(), request.getAppointmentDate(),
                    request.getAppointmentTime()).isPresent()) {
                log.warn("Booking failed: Duplicate entry found for Patient {}, Doctor {} at {} on {}.",
                        request.getPatientId(), request.getDoctorId(), request.getAppointmentTime(), request.getAppointmentDate());
                return responseGenerator.generateResponse(ResponseCode.DUPLICATE_BOOKING, MessageConstant.DUPLICATE_BOOKING, null);
            }

            // Check if doctor is already booked by ANYONE (at that time)
            List<String> activeStatuses = List.of(Status.PENDING.name(), Status.APPROVED.name());
            if (appointmentRepository.existsByDoctorAndAppointmentDateAndAppointmentTimeAndStatusIn(
                    doctorEntity.getId(), request.getAppointmentDate(), request.getAppointmentTime(), activeStatuses)) {
                log.warn("Booking failed: Doctor ID {} already has a booking at {} on {}.",
                        request.getDoctorId(), request.getAppointmentTime(), request.getAppointmentDate());
                return responseGenerator.generateResponse(ResponseCode.DOCTOR_ALREADY_BOOKED, MessageConstant.DOCTOR_ALREADY_BOOKED, null);
            }

            // Create and Save Appointment
            String bookReferenceID = generateReference();
            log.debug("Reference ID Generated: {}", bookReferenceID);

            MedisphereAppointmentEntity medisphereAppointmentEntity = new MedisphereAppointmentEntity();
            medisphereAppointmentEntity.setPatient(patientEntity);
            medisphereAppointmentEntity.setDoctor(doctorEntity);
            medisphereAppointmentEntity.setAppointmentDate(request.getAppointmentDate());
            medisphereAppointmentEntity.setAppointmentTime(request.getAppointmentTime());
            medisphereAppointmentEntity.setStatus(Status.PENDING.name());
            medisphereAppointmentEntity.setReason(request.getReason());
            medisphereAppointmentEntity.setBookReferenceId(bookReferenceID);

            appointmentRepository.save(medisphereAppointmentEntity);
            log.debug("Book Appointment Created Successfully. Reference ID: {}", bookReferenceID);

            BookAppointmentResponseDTO bookAppointmentResponseDTO = BookAppointmentResponseDTO.builder()
                    .status(medisphereAppointmentEntity.getStatus())
                    .bookReferenceID(bookReferenceID)
                    .build();

            log.debug("Booking process success.");
            return responseGenerator.generateResponse(ResponseCode.APPOINTMENT_OPERATION_SUCCESS, MessageConstant.APPOINTMENT_OPERATION_SUCCESS, bookAppointmentResponseDTO);

        } catch (Exception e) {
            log.error("Error Occurred during booking: ", e);
            return responseGenerator.generateResponse(ResponseCode.APPOINTMENT_OPERATION_FAILED, MessageConstant.APPOINTMENT_OPERATION_FAILED, null);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Object> updateAppointment(AppointmentUpdateRequest request) {
        try {
            log.debug("Appointment Update Method Called");

            MedisphereAppointmentEntity medisphereAppointmentEntity = appointmentRepository.findByBookReferenceId(request.getAppointmentReferenceId());
            if (medisphereAppointmentEntity == null) {
                log.warn("Appointment not found for reference ID: {}.", request.getAppointmentReferenceId());
                return responseGenerator.generateResponse(ResponseCode.APPOINTMENT_NOT_FOUND, MessageConstant.APPOINTMENT_NOT_FOUND, null);
            }

            if(medisphereAppointmentEntity.getStatus().equals(Status.APPROVED.name())) {
                log.warn("Appointment already Approved. Cannot be modified: {}.", medisphereAppointmentEntity.getStatus());
                return responseGenerator.generateResponse(ResponseCode.APPOINTMENT_ALREADY_APPROVED, MessageConstant.APPOINTMENT_ALREADY_APPROVED, null);
            }

            // Update fields if provided in request
            boolean isDateTimeChanged = false;

            if (request.getAppointmentDate() != null) {
                if (request.getAppointmentDate().isBefore(LocalDate.now())) {
                    log.warn("Update failed: New date {} is in the past.", request.getAppointmentDate());
                    return responseGenerator.generateResponse(ResponseCode.PAST_DATE_ERROR, MessageConstant.PAST_DATE_ERROR, null);
                }
                medisphereAppointmentEntity.setAppointmentDate(request.getAppointmentDate());
                isDateTimeChanged = true;
            }

            if (request.getAppointmentTime() != null) {
                medisphereAppointmentEntity.setAppointmentTime(request.getAppointmentTime());
                isDateTimeChanged = true;
            }

            if (request.getReason() != null) {
                medisphereAppointmentEntity.setReason(request.getReason());
            }



            // If date or time changed, check for conflicts
            if (isDateTimeChanged) {
                List<String> activeStatuses = List.of(Status.PENDING.name());
                if (appointmentRepository.existsByDoctorAndAppointmentDateAndAppointmentTimeAndStatusIn(
                        medisphereAppointmentEntity.getDoctor().getId(),
                        medisphereAppointmentEntity.getAppointmentDate(),
                        medisphereAppointmentEntity.getAppointmentTime(),
                        activeStatuses)) {

                    log.warn("Update notice: New time slot at {} on {} have a conflict.",
                            medisphereAppointmentEntity.getAppointmentTime(), medisphereAppointmentEntity.getAppointmentDate());
                    return responseGenerator.generateResponse(ResponseCode.DOCTOR_ALREADY_BOOKED, MessageConstant.DOCTOR_ALREADY_BOOKED, null);
                }
            }

            appointmentRepository.save(medisphereAppointmentEntity);
            log.debug("Appointment updated successfully: {}", request.getAppointmentReferenceId());

            AppointmentUpdateResponseDTO appointmentUpdateResponseDTO = AppointmentUpdateResponseDTO.builder()
                    .status(medisphereAppointmentEntity.getStatus())
                    .bookReferenceID(request.getAppointmentReferenceId())
                    .build();

            return responseGenerator.generateResponse(ResponseCode.APPOINTMENT_OPERATION_SUCCESS,
                    MessageConstant.APPOINTMENT_OPERATION_SUCCESS, appointmentUpdateResponseDTO);

        } catch (Exception e) {
            log.error("Error occurred during appointment update: ", e);
            return responseGenerator.generateResponse(ResponseCode.APPOINTMENT_OPERATION_FAILED,
                    MessageConstant.APPOINTMENT_OPERATION_FAILED, null);
        }
    }

    private String generateReference() {
        return "MEDSPREF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
