package com.medisphere.appointment.repository;

import com.medisphere.appointment.entity.MedisphereAppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<MedisphereAppointmentEntity, Integer> {


    @Query("SELECT a FROM MedisphereAppointmentEntity a " +
            "WHERE a.patient.id = :patientId AND a.doctor.id = :doctorId AND a.appointmentDate = :appointmentDate AND a.appointmentTime = :appointmentTime")
    Optional<MedisphereAppointmentEntity> findByPatientAndDoctorAndAppointmentDateAndAppointmentTime(
            @Param("patientId") String patientId,
            @Param("doctorId") String doctorId,
            @Param("appointmentDate") LocalDate appointmentDate,
            @Param("appointmentTime") LocalTime appointmentTime
    );

    @Query("SELECT a FROM MedisphereAppointmentEntity a " +
            "WHERE a.doctor.id = :doctorId AND a.appointmentDate = :appointmentDate AND a.appointmentTime = :appointmentTime AND a.status IN :statuses")
    boolean existsByDoctorAndAppointmentDateAndAppointmentTimeAndStatusIn(
            @Param("doctorId") String doctorId,
            @Param("appointmentDate") LocalDate appointmentDate,
            @Param("appointmentTime") LocalTime appointmentTime,
            @Param("statuses") Collection<String> statuses

    );

}
