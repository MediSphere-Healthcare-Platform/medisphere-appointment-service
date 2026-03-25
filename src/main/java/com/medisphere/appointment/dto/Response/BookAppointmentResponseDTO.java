package com.medisphere.appointment.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookAppointmentResponseDTO {
    private String status;
    private Object bookReferenceID;
}
