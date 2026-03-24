package com.medisphere.appointment.controller;

import com.medisphere.appointment.domain.GetDoctorsBySpecialityRequest;
import com.medisphere.appointment.dto.Request.GetDoctorsBySpecialityRequestDTO;
import com.medisphere.appointment.service.AppointmentService;
import com.medisphere.appointment.util.Utility;
import jakarta.validation.Valid;
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
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Validated
public class AppointmentController {

    public final AppointmentService appointmentService;
    public final ModelMapper modelMapper;

    @PostMapping(value = EndPoint.GET_DOCTORS_BY_SPECIALITY)
    public ResponseEntity<Object> getDoctorsBySpeciality(@Valid @RequestBody GetDoctorsBySpecialityRequestDTO requestDTO) {
        log.info("Received request to get doctors by speciality: {}", Utility.objectToJson(requestDTO));
        return appointmentService.getDoctorsBySpeciality(modelMapper.map(requestDTO, GetDoctorsBySpecialityRequest.class));
    }

}
