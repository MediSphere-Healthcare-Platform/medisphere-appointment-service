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
public class AppointmentsByDoctorIdRequestDTO {

    @NotNull(message = "Doctor ID cannot be null")
    @NotEmpty(message = "Doctor ID cannot be empty")
    private String doctorId;

}
