package com.medisphere.appointment.repository;

import com.medisphere.appointment.entity.MedisphereAppointmentEntity;
import com.medisphere.appointment.entity.MedispherePatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<MedispherePatientEntity, String> {

        @Query("SELECT p FROM MedispherePatientEntity p " +
                "WHERE p.patientId = :patientId AND p.status = :status")
        MedispherePatientEntity findByIdAndStatus(
                @Param("patientId") String patientId,
                @Param("status") String status
        );

}
