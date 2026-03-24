package com.medisphere.appointment.service;

import org.springframework.http.ResponseEntity;

import java.util.Locale;

public interface ResponseGenerator {

    ResponseEntity<Object> generateResponse(String code, String description, Object data);

}
