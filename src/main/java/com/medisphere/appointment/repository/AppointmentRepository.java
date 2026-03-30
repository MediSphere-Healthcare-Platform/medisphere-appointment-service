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
            "WHERE a.patient.id = :patId AND a.doctor.id = :docId AND a.appointmentDate = :appointmentDate AND a.appointmentTime = :appointmentTime")
    Optional<MedisphereAppointmentEntity> findByPatientAndDoctorAndAppointmentDateAndAppointmentTime(
            @Param("patId") Integer patId,
            @Param("docId") Integer docId,
            @Param("appointmentDate") LocalDate appointmentDate,
            @Param("appointmentTime") LocalTime appointmentTime
    );


    @Query("SELECT (COUNT(a) > 0) FROM MedisphereAppointmentEntity a " +
            "WHERE a.doctor.id = :docId AND a.appointmentDate = :appointmentDate AND a.appointmentTime = :appointmentTime AND a.status IN :statuses")
    boolean existsByDoctorAndAppointmentDateAndAppointmentTimeAndStatusIn(
            @Param("docId") Integer docId,
            @Param("appointmentDate") LocalDate appointmentDate,
            @Param("appointmentTime") LocalTime appointmentTime,
            @Param("statuses") Collection<String> statuses
    );

    MedisphereAppointmentEntity findByBookReferenceId(String bookReferenceId);
}
