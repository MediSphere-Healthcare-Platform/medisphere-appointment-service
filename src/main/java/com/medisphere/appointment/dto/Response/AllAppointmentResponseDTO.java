package com.medisphere.appointment.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
public class AllAppointmentResponseDTO {
    private int id;
    private String patientId;
    private String doctorId;
    private String msUserID;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String status;
    private String reason;
    private LocalDateTime createDate;
    private LocalDateTime modifiedDate;
    private String appointmentRefId;

    public AllAppointmentResponseDTO(int id, String patientId, String doctorId, String msUserID, LocalDate appointmentDate, LocalTime appointmentTime, String status, String reason, LocalDateTime createDate, LocalDateTime modifiedDate, String appointmentRefId) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.msUserID = msUserID;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.reason = reason;
        this.createDate = createDate;
        this.modifiedDate = modifiedDate;
        this.appointmentRefId = appointmentRefId;
    }
}
