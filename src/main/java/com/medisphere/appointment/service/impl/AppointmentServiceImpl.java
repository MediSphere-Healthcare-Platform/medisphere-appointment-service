package com.medisphere.appointment.service.impl;

import com.medisphere.appointment.domain.GetDoctorsBySpecialityRequest;
import com.medisphere.appointment.dto.Response.GetDoctorsBySpecialityResponseDTO;
import com.medisphere.appointment.entity.TestDoctorsDatum;
import com.medisphere.appointment.repository.TestDoctorsDatumRepository;
import com.medisphere.appointment.service.AppointmentService;
import com.medisphere.appointment.service.ResponseGenerator;
import com.medisphere.appointment.util.MessageConstant;
import com.medisphere.appointment.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class AppointmentServiceImpl implements AppointmentService {

    private final TestDoctorsDatumRepository testDoctorsDatumRepository;
    private final ResponseGenerator responseGenerator;

    public ResponseEntity<Object> getDoctorsBySpeciality(GetDoctorsBySpecialityRequest request) {
        try {
            log.debug("Get Doctors By Speciality Called.");

            List<TestDoctorsDatum> doc = testDoctorsDatumRepository.findTestDoctorsDatumBySpecialty(request.getSpeciality());
            log.debug("Doctors Retrieved: {}", doc.size());
            if (doc.isEmpty()) {
                log.debug("No Doctors For The Given Speciality");
                return responseGenerator.generateResponse(ResponseCode.DOCTORS_NOT_FOUND, MessageConstant.DOCTORS_NOT_FOUND, null);
            }

            GetDoctorsBySpecialityResponseDTO getDoctorsBySpecialityResponseDTO = GetDoctorsBySpecialityResponseDTO.builder()
                    .speciality(request.getSpeciality())
                    .data(doc)
                    .build();

            log.debug("Doctors Retrieval Success.");
            return responseGenerator.generateResponse(ResponseCode.APPOINTMENT_OPERATION_SUCCESS, MessageConstant.DOCTORS_RETRIEVAL_SUCCESS, getDoctorsBySpecialityResponseDTO);

        } catch (Exception e) {
            log.error("Error Occurred: ", e);
            return responseGenerator.generateResponse(ResponseCode.APPOINTMENT_OPERATION_FAILED, MessageConstant.APPOINTMENT_OPERATION_FAILED, null);
        }
    }




}
