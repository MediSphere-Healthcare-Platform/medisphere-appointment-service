package com.medisphere.appointment.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AllDoctorsClientResponse {
    private int code;
    private String message;
    private List<DoctorClientResponse> data;
}
