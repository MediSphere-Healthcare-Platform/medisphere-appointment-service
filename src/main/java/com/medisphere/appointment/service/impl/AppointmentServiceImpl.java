package com.medisphere.appointment.service.impl;

import com.medisphere.appointment.client.MedisphereDoctorClient;
import com.medisphere.appointment.client.MedisphereNotificationClient;
import com.medisphere.appointment.client.MedispherePatientClient;
import com.medisphere.appointment.client.request.NotificationClientRequest;
import com.medisphere.appointment.client.response.AllDoctorsClientResponse;
import com.medisphere.appointment.client.response.DoctorByIdClientResponse;
import com.medisphere.appointment.client.response.DoctorClientResponse;
import com.medisphere.appointment.client.response.PatientByIdClientResponse;
import com.medisphere.appointment.client.response.PatientClientResponse;
import com.medisphere.appointment.domain.*;
import com.medisphere.appointment.dto.Response.*;
import com.medisphere.appointment.entity.CommonUrlEntity;
import com.medisphere.appointment.entity.MedisphereAppointmentEntity;
import com.medisphere.appointment.exception.ServiceException;
import com.medisphere.appointment.repository.AppointmentRepository;
import com.medisphere.appointment.repository.CommonUrlRepository;
import com.medisphere.appointment.service.AppointmentService;
import com.medisphere.appointment.service.ResponseGenerator;
import com.medisphere.appointment.util.MessageConstant;
import com.medisphere.appointment.util.ResponseCode;
import com.medisphere.appointment.util.Utility;
import com.medisphere.appointment.util.enums.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
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

    private final ResponseGenerator responseGenerator;
    private final AppointmentRepository appointmentRepository;
    private final ModelMapper modelMapper;
    private final MedisphereDoctorClient medisphereDoctorClient;
    private final MedispherePatientClient medispherePatientClient;
    private final MedisphereNotificationClient medisphereNotificationClient;
    private final CommonUrlRepository commonUrlRepository;

    @Override
    public ResponseEntity<Object> getAllAppointments() {
        try {
            log.debug("Get All Appointments Called.");

            List<AllAppointmentResponseDTO> allAppointmentResponseDTOList = appointmentRepository.findAllAppointments();
            log.debug("Total Appointments Retrieved: {}", allAppointmentResponseDTOList.size());

            if (allAppointmentResponseDTOList.isEmpty()) {
                log.warn("No Appointments Found.");
                return responseGenerator.generateResponse(ResponseCode.APPOINTMENT_NOT_FOUND,
                        MessageConstant.APPOINTMENT_NOT_FOUND, null);
            }

            log.debug("Appointments retrieval success.");
            return responseGenerator.generateResponse(ResponseCode.APPOINTMENT_OPERATION_SUCCESS,
                    MessageConstant.APPOINTMENT_OPERATION_SUCCESS, allAppointmentResponseDTOList);

        } catch (Exception e) {
            log.error("Error Occurred: ", e);
            return responseGenerator.generateResponse(ResponseCode.APPOINTMENT_OPERATION_FAILED,
                    MessageConstant.APPOINTMENT_OPERATION_FAILED, null);
        }
    }

    @Override
    public ResponseEntity<Object> getDoctorsBySpeciality(GetDoctorsBySpecialityRequest request) {
        try {
            log.debug("Get Doctors By Speciality Called.");

            // Validate Doctor Existence and Status
            ResponseEntity<AllDoctorsClientResponse> allDoctorsClientResponse = medisphereDoctorClient.getAllDoctors();
            AllDoctorsClientResponse allDoctorEntities = allDoctorsClientResponse.getBody();
            log.debug("Doctors Retrieved: {}", allDoctorEntities);
            if (allDoctorEntities == null) {
                log.warn("Get All Doctors failed: Doctors not found.");
                return responseGenerator.generateResponse(ResponseCode.DOCTOR_NOT_FOUND,
                        MessageConstant.DOCTOR_NOT_FOUND, null);
            }

            // Map Entity to DTO and filter by speciality and active status
            List<DoctorDetailDTO> doctorDetails = java.util.Optional.ofNullable(allDoctorEntities.getData())
                    .orElse(java.util.Collections.emptyList()).stream()
                    .filter(entity -> Status.active.name().equalsIgnoreCase(entity.getStatus())
                            && request.getSpeciality().equals(entity.getSpecialty()))
                    .map(entity -> {
                        DoctorDetailDTO dto = modelMapper.map(entity, DoctorDetailDTO.class);
                        dto.setDrName(entity.getFirstName() + " " + entity.getLastName());
                        dto.setDoctorId(entity.getDoctorId());
                        return dto;
                    })

                    .collect(Collectors.toList());

            DoctorsBySpecialityResponseDTO doctorsBySpecialityResponseDTO = DoctorsBySpecialityResponseDTO
                    .builder()
                    .speciality(request.getSpeciality())
                    .data(doctorDetails)
                    .build();

            log.debug("Doctors Retrieval Success.");
            return responseGenerator.generateResponse(ResponseCode.APPOINTMENT_OPERATION_SUCCESS,
                    MessageConstant.DOCTORS_RETRIEVAL_SUCCESS, doctorsBySpecialityResponseDTO);

        } catch (Exception e) {
            log.error("Error Occurred: ", e);
            return responseGenerator.generateResponse(ResponseCode.APPOINTMENT_OPERATION_FAILED,
                    MessageConstant.APPOINTMENT_OPERATION_FAILED, null);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Object> bookAppointment(BookAppointmentRequest request) {
        try {
            // TODO: After success send email and sms.

            log.debug("Book Appointment Called.");

            // Validate Appointment Date (Must be today or in the future)
            if (request.getAppointmentDate().isBefore(LocalDate.now())) {
                log.warn("Booking failed: Date {} is in the past.", request.getAppointmentDate());
                return responseGenerator.generateResponse(ResponseCode.PAST_DATE_ERROR, MessageConstant.PAST_DATE_ERROR,
                        null);
            }

            // Validate Active Patient Existence
            PatientClientResponse patientEntity = getValidatedPatient(request.getPatientId());

            // Validate Doctor Existence and Status
            DoctorClientResponse doctorEntity = getValidatedDoctor(request.getDoctorId());

            // Check for Duplicate Booking (Same patient, doctor, date, and time)
            if (appointmentRepository
                    .findByPatientAndDoctorAndAppointmentDateAndAppointmentTime(
                            patientEntity.getPatientId(),
                            doctorEntity.getDoctorId(), request.getAppointmentDate(),
                            request.getAppointmentTime())
                    .isPresent()) {
                log.warn("Booking failed: Duplicate entry found for Patient {}, Doctor {} at {} on {}.",
                        request.getPatientId(), request.getDoctorId(),
                        request.getAppointmentTime(), request.getAppointmentDate());
                return responseGenerator.generateResponse(ResponseCode.DUPLICATE_BOOKING,
                        MessageConstant.DUPLICATE_BOOKING, null);
            }

            // Check if doctor is already booked by ANYONE (at that time)
            List<String> activeStatuses = List.of(Status.PENDING.name(), Status.APPROVED.name());
            if (appointmentRepository.existsByDoctorAndAppointmentDateAndAppointmentTimeAndStatusIn(
                    doctorEntity.getDoctorId(), request.getAppointmentDate(),
                    request.getAppointmentTime(), activeStatuses)) {
                log.warn("Booking failed: Doctor ID {} already has a booking at {} on {}.",
                        request.getDoctorId(), request.getAppointmentTime(),
                        request.getAppointmentDate());
                return responseGenerator.generateResponse(ResponseCode.DOCTOR_ALREADY_BOOKED,
                        MessageConstant.DOCTOR_ALREADY_BOOKED, null);
            }

            // Create and Save Appointment
            String appointmentReferenceID = generateReference();
            log.debug("Reference ID Generated: {}", appointmentReferenceID);

            MedisphereAppointmentEntity medisphereAppointmentEntity = new MedisphereAppointmentEntity();
            medisphereAppointmentEntity.setPatientId(patientEntity.getPatientId());
            medisphereAppointmentEntity.setDoctorId(doctorEntity.getDoctorId());
            medisphereAppointmentEntity.setAppointmentDate(request.getAppointmentDate());
            medisphereAppointmentEntity.setAppointmentTime(request.getAppointmentTime());
            medisphereAppointmentEntity.setStatus(Status.PENDING.name());
            medisphereAppointmentEntity.setReason(request.getReason());
            medisphereAppointmentEntity.setMsUserId(request.getMsUserId());
            medisphereAppointmentEntity.setAppointmentReferenceId(appointmentReferenceID);

            appointmentRepository.save(medisphereAppointmentEntity);
            log.debug("Book Appointment Created Successfully. Reference ID: {}", appointmentReferenceID);

            BookAppointmentResponseDTO bookAppointmentResponseDTO = BookAppointmentResponseDTO.builder()
                    .status(medisphereAppointmentEntity.getStatus())
                    .bookReferenceID(appointmentReferenceID)
                    .build();

            log.debug("Booking process success.");
            return responseGenerator.generateResponse(ResponseCode.APPOINTMENT_OPERATION_SUCCESS,
                    MessageConstant.APPOINTMENT_OPERATION_SUCCESS, bookAppointmentResponseDTO);

        } catch (Exception e) {
            log.error("Error Occurred during booking: ", e);
            return responseGenerator.generateResponse(ResponseCode.APPOINTMENT_OPERATION_FAILED,
                    MessageConstant.APPOINTMENT_OPERATION_FAILED, null);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Object> updateAppointment(AppointmentUpdateRequest request) {
        try {
            log.debug("Appointment Update Method Called");

            MedisphereAppointmentEntity medisphereAppointmentEntity = appointmentRepository
                    .findByAppointmentReferenceId(request.getAppointmentReferenceId());
            log.debug("Appointment entity {}:", Utility.objectToJson(medisphereAppointmentEntity));
            if (medisphereAppointmentEntity == null) {
                log.warn("Appointment not found for reference ID: {}.", request.getAppointmentReferenceId());
                return responseGenerator.generateResponse(ResponseCode.APPOINTMENT_NOT_FOUND,
                        MessageConstant.APPOINTMENT_NOT_FOUND, null);
            }

            if (medisphereAppointmentEntity.getStatus().equals(Status.APPROVED.name())) {
                log.warn("Appointment already Approved. Cannot be modified: {}.",
                        medisphereAppointmentEntity.getStatus());
                return responseGenerator.generateResponse(ResponseCode.APPOINTMENT_ALREADY_APPROVED,
                        MessageConstant.APPOINTMENT_ALREADY_APPROVED, null);
            }

            // Update fields if provided in request
            boolean isDateTimeChanged = false;
            boolean isDoctorChanged = false;

            if (request.getAppointmentDate() != null
                    && !request.getAppointmentDate().equals(medisphereAppointmentEntity.getAppointmentDate())) {
                if (request.getAppointmentDate().isBefore(LocalDate.now())) {
                    log.warn("Update failed: New date {} is in the past.", request.getAppointmentDate());
                    return responseGenerator.generateResponse(ResponseCode.PAST_DATE_ERROR,
                            MessageConstant.PAST_DATE_ERROR, null);
                }
                medisphereAppointmentEntity.setAppointmentDate(request.getAppointmentDate());
                isDateTimeChanged = true;
            }

            if (request.getAppointmentTime() != null && !request.getAppointmentTime()
                    .equals(medisphereAppointmentEntity.getAppointmentTime())) {
                medisphereAppointmentEntity.setAppointmentTime(request.getAppointmentTime());
                isDateTimeChanged = true;
            }

            if (request.getReason() != null) {
                medisphereAppointmentEntity.setReason(request.getReason());
            }

            if (request.getDoctorId() != null
                    && !request.getDoctorId().equals(medisphereAppointmentEntity.getDoctorId())) {
                // Validate Doctor Existence and Status
                getValidatedDoctor(request.getDoctorId());

                medisphereAppointmentEntity.setDoctorId(request.getDoctorId());
                isDoctorChanged = true;
            }

            // If date, time, or doctor changed, check for conflicts
            if (isDateTimeChanged || isDoctorChanged) {
                List<String> activeStatuses = List.of(Status.PENDING.name(), Status.APPROVED.name());

                // Use the updated values from the entity (which are already updated if
                // provided, or kept from before)
                if (appointmentRepository
                        .existsByDoctorAndAppointmentDateAndAppointmentTimeAndStatusInAndAppointmentReferenceIdNot(
                                medisphereAppointmentEntity.getDoctorId(),
                                medisphereAppointmentEntity.getAppointmentDate(),
                                medisphereAppointmentEntity.getAppointmentTime(),
                                activeStatuses,
                                medisphereAppointmentEntity.getAppointmentReferenceId())) {

                    log.warn("Update notice: New appointment details for Doctor ID {} at {} on {} have a conflict.",
                            medisphereAppointmentEntity.getDoctorId(),
                            medisphereAppointmentEntity.getAppointmentTime(),
                            medisphereAppointmentEntity.getAppointmentDate());
                    return responseGenerator.generateResponse(ResponseCode.DOCTOR_ALREADY_BOOKED,
                            MessageConstant.DOCTOR_ALREADY_BOOKED, null);
                }
            }

            if (Status.PENDING.name().equalsIgnoreCase(medisphereAppointmentEntity.getStatus())) {
                // Future logic for pending appointments can go here
            }

            appointmentRepository.save(medisphereAppointmentEntity);
            log.debug("Appointment updated successfully: {}", request.getAppointmentReferenceId());

            AppointmentUpdateResponseDTO appointmentUpdateResponseDTO = AppointmentUpdateResponseDTO
                    .builder()
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

    @Override
    @Transactional
    public ResponseEntity<Object> cancelAppointment(AppointmentCancelRequest request) {
        try {
            log.debug("Appointment Cancel Method Called");

            MedisphereAppointmentEntity medisphereAppointmentEntity = appointmentRepository
                    .findByAppointmentReferenceId(request.getAppointmentReferenceId());
            log.debug("Appointment entity {}:", Utility.objectToJson(medisphereAppointmentEntity));
            if (medisphereAppointmentEntity == null) {
                log.warn("Appointment not found for reference ID: {}.", request.getAppointmentReferenceId());
                return responseGenerator.generateResponse(ResponseCode.APPOINTMENT_NOT_FOUND,
                        MessageConstant.APPOINTMENT_NOT_FOUND, null);
            }

            if (Status.CANCELLED.name().equalsIgnoreCase(medisphereAppointmentEntity.getStatus())) {
                log.warn("Appointment already cancelled: {}.", request.getAppointmentReferenceId());
                return responseGenerator.generateResponse(ResponseCode.APPOINTMENT_ALREADY_CANCELLED,
                        MessageConstant.APPOINTMENT_ALREADY_CANCELLED, null);
            }

            medisphereAppointmentEntity.setStatus(Status.CANCELLED.name());
            appointmentRepository.delete(medisphereAppointmentEntity);

            log.info("Appointment cancelled successfully: {}", request.getAppointmentReferenceId());
            return responseGenerator.generateResponse(ResponseCode.APPOINTMENT_OPERATION_SUCCESS,
                    MessageConstant.APPOINTMENT_OPERATION_SUCCESS, null);

        } catch (Exception e) {
            log.error("Error occurred during appointment cancellation: ", e);
            return responseGenerator.generateResponse(ResponseCode.APPOINTMENT_OPERATION_FAILED,
                    MessageConstant.APPOINTMENT_OPERATION_FAILED, null);
        }
    }

    @Override
    public ResponseEntity<Object> trackAppointmentStatus(AppointmentTrackRequest request) {
        try {
            log.debug("Track Appointment Status Called.");

            MedisphereAppointmentEntity medisphereAppointmentEntity = appointmentRepository
                    .findByAppointmentReferenceId(request.getAppointmentReferenceId());
            log.debug("Appointment entity {}:", Utility.objectToJson(medisphereAppointmentEntity));
            if (medisphereAppointmentEntity == null) {
                log.warn("Appointment not found for reference ID: {}.", request.getAppointmentReferenceId());
                return responseGenerator.generateResponse(ResponseCode.APPOINTMENT_NOT_FOUND,
                        MessageConstant.APPOINTMENT_NOT_FOUND, null);
            }

            ResponseEntity<DoctorByIdClientResponse> doctorByIdClientResponse = medisphereDoctorClient
                    .getDoctorById(medisphereAppointmentEntity.getDoctorId());
            DoctorByIdClientResponse doctorResponse = doctorByIdClientResponse.getBody();
            DoctorClientResponse doctorEntity = (doctorResponse != null) ? doctorResponse.getData() : null;

            log.debug("Doctor Retrieved for ID {}: {}", medisphereAppointmentEntity.getDoctorId(),
                    doctorEntity != null ? "Found" : "Not Found");
            if (doctorEntity == null) {
                log.warn("Appointment update failed: Doctor ID {} not found.",
                        medisphereAppointmentEntity.getDoctorId());
                return responseGenerator.generateResponse(ResponseCode.DOCTOR_NOT_FOUND,
                        MessageConstant.DOCTOR_NOT_FOUND, null);
            }

            TrackAppointmentStatusResponseDTO responseDTO = TrackAppointmentStatusResponseDTO.builder()
                    .appointmentReferenceId(medisphereAppointmentEntity.getAppointmentReferenceId())
                    .patientId(medisphereAppointmentEntity.getPatientId())
                    .doctorId(medisphereAppointmentEntity.getDoctorId())
                    .doctorName(doctorEntity.getFirstName() + " " + doctorEntity.getLastName())
                    .appointmentDate(medisphereAppointmentEntity.getAppointmentDate())
                    .appointmentTime(medisphereAppointmentEntity.getAppointmentTime())
                    .status(medisphereAppointmentEntity.getStatus())
                    .build();

            log.info("Tracking info retrieved for: {}", request.getAppointmentReferenceId());
            return responseGenerator.generateResponse(ResponseCode.APPOINTMENT_OPERATION_SUCCESS,
                    MessageConstant.APPOINTMENT_OPERATION_SUCCESS, responseDTO);

        } catch (Exception e) {
            log.error("Error occurred while tracking appointment status: ", e);
            return responseGenerator.generateResponse(ResponseCode.APPOINTMENT_OPERATION_FAILED,
                    MessageConstant.APPOINTMENT_OPERATION_FAILED, null);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Object> appointmentStatusChange(AppointmentStatusChangeRequest request) {
        try {
            log.debug("Appointment Status Change Called.");

            MedisphereAppointmentEntity appointmentEntity = appointmentRepository
                    .findByAppointmentReferenceId(request.getAppointmentReferenceId());
            log.debug("Appointment entity {}:", Utility.objectToJson(appointmentEntity));
            if (appointmentEntity == null) {
                log.warn("Appointment not found for reference ID: {}.", request.getAppointmentReferenceId());
                return responseGenerator.generateResponse(ResponseCode.APPOINTMENT_NOT_FOUND,
                        MessageConstant.APPOINTMENT_NOT_FOUND, null);
            }

            // Check if current status exists
            if (request.getStatus().equalsIgnoreCase(appointmentEntity.getStatus())) {
                log.warn("Status already exists: {}", request.getStatus());
                return responseGenerator.generateResponse(ResponseCode.STATUS_ALREADY_EXISTS,
                        MessageConstant.STATUS_ALREADY_EXISTS, null);
            }

            if (request.getStatus().equalsIgnoreCase(Status.PENDING.name())) {
                log.warn("Status cannot change to pending again: {}", request.getStatus());
                return responseGenerator.generateResponse(ResponseCode.INVALID_STATUS, MessageConstant.INVALID_STATUS,
                        null);
            }

            // Validate and normalize requested status
            String normalizedStatus = null;
            for (Status s : Status.values()) {
                if (s.name().equalsIgnoreCase(request.getStatus())) {
                    normalizedStatus = s.name();
                    break;
                }
            }

            if (normalizedStatus == null) {
                log.warn("Invalid status requested: {}", request.getStatus());
                return responseGenerator.generateResponse(ResponseCode.INVALID_STATUS, MessageConstant.INVALID_STATUS,
                        null);
            }

            appointmentEntity.setStatus(normalizedStatus);
            appointmentRepository.save(appointmentEntity);
            log.info("Appointment status changed successfully to: {}", normalizedStatus);

            // Notification and Payment Workflow
            if (Status.APPROVED.name().equalsIgnoreCase(normalizedStatus)) {
                log.info("Sending notification for approved appointment: {}",
                        appointmentEntity.getAppointmentReferenceId());

                String frontendBaseUrl = commonUrlRepository.findByCode("FRONTEND_BASE_URL")
                        .map(CommonUrlEntity::getUrl)
                        .orElse("http://localhost:3000");

                String frontendPaymentUrl = commonUrlRepository.findByCode("FRONTEND_PAYMENT_URL")
                        .map(CommonUrlEntity::getUrl)
                        .orElse("/payment/initiate");

                NotificationClientRequest notificationClientRequest = NotificationClientRequest.builder()
                        .userId(appointmentEntity.getMsUserId())
                        .userRole("PATIENT")
                        .title("Appointment Approved")
                        .message("Your appointment (" + appointmentEntity.getAppointmentReferenceId()
                                + ") is APPROVED. Please complete the payment at: " + frontendBaseUrl
                                + frontendPaymentUrl + "?appointmentRefId="
                                + appointmentEntity.getAppointmentReferenceId())
                        .channel("EMAIL")
                        .relatedId(appointmentEntity.getAppointmentReferenceId())
                        .isBroadcast(false)
                        .build();

                try {
                    medisphereNotificationClient.createNotification(notificationClientRequest);
                    log.info("Notification sent successfully for appointment: {}",
                            appointmentEntity.getAppointmentReferenceId());
                } catch (Exception e) {
                    log.error("Failed to send notification for appointment: {}",
                            appointmentEntity.getAppointmentReferenceId(), e);
                }
            }

            AppointmentStatusChangeResponseDTO appointmentStatusChangeResponseDTO = AppointmentStatusChangeResponseDTO
                    .builder()
                    .appointmentReferenceId(request.getAppointmentReferenceId())
                    .status(Status.Success.name())
                    .build();

            log.info("Appointment Status Change success.");
            return responseGenerator.generateResponse(ResponseCode.APPOINTMENT_OPERATION_SUCCESS,
                    MessageConstant.APPOINTMENT_OPERATION_SUCCESS, appointmentStatusChangeResponseDTO);
        } catch (Exception e) {
            log.error("Error occurred while appointment status change: ", e);
            return responseGenerator.generateResponse(ResponseCode.APPOINTMENT_OPERATION_FAILED,
                    MessageConstant.APPOINTMENT_OPERATION_FAILED, null);
        }
    }

    @Override
    public ResponseEntity<Object> getAllAppointmentsByPatientId(AppointmentsByPatientIdRequest request) {
        try {
            log.debug("Get all appointments by patient id Called.");

            // Validate Active Patient Existence
            getValidatedPatient(request.getPatientId());

            List<MedisphereAppointmentEntity> patientAppointments = appointmentRepository
                    .findMedisphereAppointmentEntitiesByPatientId(request.getPatientId());
            if (patientAppointments.isEmpty()) {
                log.warn("No Appointments Found for patient: {}", request.getPatientId());
                return responseGenerator.generateResponse(ResponseCode.APPOINTMENT_NOT_FOUND,
                        MessageConstant.APPOINTMENT_NOT_FOUND, null);
            }

            AppointmentsByPatientIdResponseDTO appointmentsByPatientIdResponseDTO = AppointmentsByPatientIdResponseDTO
                    .builder()
                    .patientAppointments(patientAppointments)
                    .build();

            log.info("Get all appointments by patient id success.");
            return responseGenerator.generateResponse(ResponseCode.APPOINTMENT_OPERATION_SUCCESS,
                    MessageConstant.APPOINTMENT_OPERATION_SUCCESS, appointmentsByPatientIdResponseDTO);
        } catch (Exception e) {
            log.error("Error occurred: ", e);
            return responseGenerator.generateResponse(ResponseCode.APPOINTMENT_OPERATION_FAILED,
                    MessageConstant.APPOINTMENT_OPERATION_FAILED, null);
        }
    }

    @Override
    public ResponseEntity<Object> getAllAppointmentsByDoctorId(AppointmentsByDoctorIdRequest request) {
        try {
            log.debug("Get all appointments by doctor id Called.");

            // Validate Active Doctor Existence
            getValidatedDoctor(request.getDoctorId());

            List<MedisphereAppointmentEntity> doctorAppointments = appointmentRepository
                    .findMedisphereAppointmentEntitiesByDoctorId(request.getDoctorId());
            if (doctorAppointments.isEmpty()) {
                log.warn("No Appointments Found for doctor: {}", request.getDoctorId());
                return responseGenerator.generateResponse(ResponseCode.APPOINTMENT_NOT_FOUND,
                        MessageConstant.APPOINTMENT_NOT_FOUND, null);
            }

            AppointmentsByDoctorIdResponseDTO appointmentsByDoctorIdResponseDTO = AppointmentsByDoctorIdResponseDTO
                    .builder()
                    .doctorAppointments(doctorAppointments)
                    .build();

            log.info("Get all appointments by doctor id success.");
            return responseGenerator.generateResponse(ResponseCode.APPOINTMENT_OPERATION_SUCCESS,
                    MessageConstant.APPOINTMENT_OPERATION_SUCCESS, appointmentsByDoctorIdResponseDTO);
        } catch (Exception e) {
            log.error("Error occurred: ", e);
            return responseGenerator.generateResponse(ResponseCode.APPOINTMENT_OPERATION_FAILED,
                    MessageConstant.APPOINTMENT_OPERATION_FAILED, null);
        }
    }

    private String generateReference() {
        return "MEDSPREF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private PatientClientResponse getValidatedPatient(String patientId) {
        ResponseEntity<PatientByIdClientResponse> patientByIdClientResponse = medispherePatientClient
                .getPatientById(patientId);
        log.debug("Patient service response: {}", Utility.objectToJson(patientByIdClientResponse));
        PatientByIdClientResponse patientResponse = patientByIdClientResponse.getBody();
        PatientClientResponse patientEntity = (patientResponse != null) ? patientResponse.getData() : null;

        log.debug("Patient Retrieved for ID {}: {}", patientId, patientEntity != null ? "Found" : "Not Found");
        if (patientEntity == null) {
            log.warn("Patient not found for ID: {}.", patientId);
            throw new ServiceException(ResponseCode.PATIENTS_NOT_FOUND, MessageConstant.PATIENTS_NOT_FOUND);
        }
        if (!Status.active.name().equalsIgnoreCase(patientEntity.getStatus())) {
            log.warn("Patient ID {} is not active: {}", patientId, patientEntity.getStatus());
            throw new ServiceException(ResponseCode.PATIENT_NOT_ACTIVE, MessageConstant.PATIENT_NOT_ACTIVE);
        }
        return patientEntity;
    }

    private DoctorClientResponse getValidatedDoctor(String doctorId) {
        ResponseEntity<DoctorByIdClientResponse> doctorByIdClientResponse = medisphereDoctorClient
                .getDoctorById(doctorId);
        log.debug("Doctor service response: {}", Utility.objectToJson(doctorByIdClientResponse));
        DoctorByIdClientResponse doctorResponse = doctorByIdClientResponse.getBody();
        DoctorClientResponse doctorEntity = (doctorResponse != null) ? doctorResponse.getData() : null;

        log.debug("Doctor Retrieved for ID {}: {}", doctorId, doctorEntity != null ? "Found" : "Not Found");
        if (doctorEntity == null) {
            log.warn("Doctor ID {} not found.", doctorId);
            throw new ServiceException(ResponseCode.DOCTOR_NOT_FOUND, MessageConstant.DOCTOR_NOT_FOUND);
        }
        if (!Status.active.name().equalsIgnoreCase(doctorEntity.getStatus())) {
            log.warn("Doctor ID {} is not active: {}", doctorId, doctorEntity.getStatus());
            throw new ServiceException(ResponseCode.DOCTOR_NOT_ACTIVE, MessageConstant.DOCTOR_NOT_ACTIVE);
        }
        return doctorEntity;
    }

}
