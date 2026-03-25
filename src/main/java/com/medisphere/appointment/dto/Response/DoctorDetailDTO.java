package com.medisphere.appointment.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoctorDetailDTO {
    private Integer id;
    private String drName;
    private String specialty;
    private String drContactNo;
}
