package com.medisphere.appointment.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoctorByIdClientResponse {
    private int id;
    private String firstName;
    private String lastName;
    private String doctorId;
    private String msUserId;
    private String specialty;
    private String contactNo;
    private String status;

}
