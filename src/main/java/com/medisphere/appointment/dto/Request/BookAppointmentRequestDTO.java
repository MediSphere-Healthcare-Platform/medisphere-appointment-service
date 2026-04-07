package com.medisphere.appointment.dto.Request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookAppointmentRequestDTO {

    @NotNull(message = "Patient ID cannot be null")
    @NotEmpty(message = "Patient ID cannot be empty")
    private String patientId;

    @NotNull(message = "Doctor ID cannot be null")
    @NotEmpty(message = "Speciality cannot be empty")
    private String doctorId;

    @NotNull(message = "Appointment date cannot be null")
    private LocalDate appointmentDate;

    @NotNull(message = "Appointment time cannot be null")
    private LocalTime appointmentTime;

    @NotNull(message = "Reason cannot be null")
    @NotEmpty(message = "Reason cannot be empty")
    private String reason;

}
