package com.medisphere.appointment.repository;

import com.medisphere.appointment.entity.MedisphereAppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<MedisphereAppointmentEntity, Integer> {

    Optional<MedisphereAppointmentEntity> findByPatientIdAndDoctorAndAppointmentDateAndAppointmentTime(
            Integer patientId, Integer doctor, LocalDate appointmentDate, LocalTime appointmentTime);

    boolean existsByDoctorAndAppointmentDateAndAppointmentTimeAndStatusIn(
            Integer doctor, LocalDate appointmentDate, LocalTime appointmentTime, Collection<String> statuses);
}
