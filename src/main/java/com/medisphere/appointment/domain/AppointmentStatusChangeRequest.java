package com.medisphere.appointment.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentStatusChangeRequest {
    private String appointmentReferenceId;
    private String status;

}
