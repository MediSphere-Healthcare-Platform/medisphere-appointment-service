package com.medisphere.appointment.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoctorByIdClientResponse {
    private int code;
    private String message;
    private DoctorClientResponse data;
}
