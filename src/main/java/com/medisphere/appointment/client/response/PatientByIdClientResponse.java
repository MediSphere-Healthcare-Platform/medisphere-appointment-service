package com.medisphere.appointment.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientByIdClientResponse {
    private int id;
    private String firstName;
    private String lastName;
    private String patientId;
    private String msUserId;
    private String contactNo;
    private String status;

}
