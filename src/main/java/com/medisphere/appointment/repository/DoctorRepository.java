package com.medisphere.appointment.repository;

import com.medisphere.appointment.entity.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<DoctorEntity, String> {

        List<DoctorEntity> findTestDoctorsDatumByStatusAndSpecialty(String status, String specialty);

        DoctorEntity findDoctorByDoctorIdAndSpecialty(String doctorId, String specialty);

}
