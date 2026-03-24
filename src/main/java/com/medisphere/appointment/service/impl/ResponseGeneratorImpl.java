package com.medisphere.appointment.service.impl;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.medisphere.appointment.service.ResponseGenerator;
import com.medisphere.appointment.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Locale;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
@Log4j2
public class ResponseGeneratorImpl implements ResponseGenerator {

    @Override
    public ResponseEntity<Object> generateResponse(String code, String description, Object data) {

        String status = "";
        String intiDescription = "";
        if (ResponseCode.APPOINTMENT_OPERATION_SUCCESS.equalsIgnoreCase(code)) {
            status = "00";
            intiDescription = "SUCCESS";
        } else {
            status = "01";
            intiDescription = "FAIL";
        }

        MainResponse response = MainResponse.builder()
                .status(status)
                .description(intiDescription)
                .data(data)
                .error(new AppointmentError(code, description))
                .build();
        return ResponseEntity.ok(response);
    }

    @Data
    @Builder
    @JsonPropertyOrder({ "status", "description", "data", "error" })
    private static class MainResponse {
        private String status;
        private String description;
        private Object data;
        private AppointmentError error;
    }

    @Data
    @Builder
    private static class AppointmentError {
        private String errorCode;
        private String errorDescription;

    }
}
