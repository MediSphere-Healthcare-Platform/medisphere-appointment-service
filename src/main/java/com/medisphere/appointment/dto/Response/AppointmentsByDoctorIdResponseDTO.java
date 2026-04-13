package com.medisphere.appointment.dto.Response;

import com.medisphere.appointment.entity.MedisphereAppointmentEntity;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppointmentsByDoctorIdResponseDTO {

    private List<MedisphereAppointmentEntity> doctorAppointments;

}
