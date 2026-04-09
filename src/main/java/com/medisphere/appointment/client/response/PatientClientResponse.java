package com.medisphere.appointment.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientClientResponse {
    private Integer id;
    private String firstName;
    private String lastName;
    private String patientId;
    private String msUserId;
    private String contactNo;
    private String status;
}
