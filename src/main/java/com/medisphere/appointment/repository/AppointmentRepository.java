package com.medisphere.appointment.repository;

import com.medisphere.appointment.dto.Response.AllAppointmentResponseDTO;
import com.medisphere.appointment.entity.MedisphereAppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<MedisphereAppointmentEntity, Integer> {

    @Query("SELECT DISTINCT new com.medisphere.appointment.dto.Response.AllAppointmentResponseDTO(" +
            "a.id, a.patientId, a.doctorId, a.msUserId, a.appointmentDate, a.appointmentTime, a.status, a.reason, a.createDate, a.modifiedDate, a.appointmentReferenceId) " +
            "FROM MedisphereAppointmentEntity a")
    List<AllAppointmentResponseDTO> findAllAppointments();

    @Query("SELECT a FROM MedisphereAppointmentEntity a " +
            "WHERE a.patientId = :patId AND a.doctorId = :docId AND a.appointmentDate = :appointmentDate AND a.appointmentTime = :appointmentTime")
    Optional<MedisphereAppointmentEntity> findByPatientAndDoctorAndAppointmentDateAndAppointmentTime(
            @Param("patId") Integer patId,
            @Param("docId") Integer docId,
            @Param("appointmentDate") LocalDate appointmentDate,
            @Param("appointmentTime") LocalTime appointmentTime
    );


    @Query("SELECT (COUNT(a) > 0) FROM MedisphereAppointmentEntity a " +
            "WHERE a.doctorId = :doctorId AND a.appointmentDate = :appointmentDate AND a.appointmentTime = :appointmentTime AND a.status IN :statuses")
    boolean existsByDoctorAndAppointmentDateAndAppointmentTimeAndStatusIn(
            @Param("docId") String doctorId,
            @Param("appointmentDate") LocalDate appointmentDate,
            @Param("appointmentTime") LocalTime appointmentTime,
            @Param("statuses") Collection<String> statuses
    );

    MedisphereAppointmentEntity findByBookReferenceId(String bookReferenceId);
}
