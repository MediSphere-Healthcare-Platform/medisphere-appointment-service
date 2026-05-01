package com.medisphere.appointment.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrackAppointmentStatusResponseDTO {

    private String appointmentReferenceId;
    private String doctorName;
    private String doctorId;
    private String patientId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String status;

}
