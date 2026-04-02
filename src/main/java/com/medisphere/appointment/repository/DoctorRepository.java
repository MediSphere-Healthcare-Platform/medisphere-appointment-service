package com.medisphere.appointment.repository;

import com.medisphere.appointment.entity.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<DoctorEntity, String> {

        List<DoctorEntity> findByStatusAndSpecialty(String status, String specialty);

        DoctorEntity findByDoctorId(String doctorId);

        DoctorEntity findDoctorByDoctorIdAndSpecialty(String doctorId, String specialty);

}
