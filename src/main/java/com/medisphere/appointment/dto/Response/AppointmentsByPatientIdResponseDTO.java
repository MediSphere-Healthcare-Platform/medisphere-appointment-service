package com.medisphere.appointment.dto.Response;

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
public class AppointmentsByPatientIdResponseDTO {

    @NotNull(message = "Patient ID cannot be null")
    @NotEmpty(message = "Patient ID cannot be empty")
    private String patientId;

}
