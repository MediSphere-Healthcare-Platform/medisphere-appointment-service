package com.medisphere.appointment.dto.Request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoctorsBySpecialityRequestDTO {

    @NotNull(message = "Speciality cannot be null")
    @NotEmpty(message = "Speciality cannot be empty")
    private String speciality;
}
