package com.medisphere.appointment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "test_doctors_data")
public class TestDoctorsDatum {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "specialty", nullable = false)
    private String specialty;

    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column(name = "consultation_fee", precision = 10)
    private BigDecimal consultationFee;


}