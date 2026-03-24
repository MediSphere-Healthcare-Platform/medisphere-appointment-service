package com.medisphere.appointment.repository;

import com.medisphere.appointment.entity.TestDoctorsDatum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestDoctorsDatumRepository extends JpaRepository<TestDoctorsDatum, String> {

        List<TestDoctorsDatum> findTestDoctorsDatumBySpecialty(String specialty);

}
