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
public class AppointmentStatusChangeRequestDTO {

    @NotNull(message = "Appointment reference ID cannot be null")
    @NotEmpty(message = "Appointment reference ID cannot be empty")
    private String appointmentReferenceId;

    @NotNull(message = "Status cannot be null")
    @NotEmpty(message = "Status cannot be empty")
    private String status;

}
