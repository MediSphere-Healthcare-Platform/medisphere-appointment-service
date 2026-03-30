package com.medisphere.appointment.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentUpdateRequest {

    private String appointmentReferenceId;

    private LocalDate appointmentDate;

    private LocalTime appointmentTime;

    private String reason;

    private String status;

}
