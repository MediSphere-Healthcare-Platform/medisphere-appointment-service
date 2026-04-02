package com.medisphere.appointment.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoctorsBySpecialityResponseDTO {
    private String speciality;
    private Object data;
}
