package com.medisphere.appointment.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Table(name = "medisphere_patient")
public class MedispherePatientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 50)
    @NotNull
    @Column(name = "patient_id", nullable = false, length = 50)
    private String patientId;

    @Size(max = 50)
    @Column(name = "address", length = 50)
    private String address;

    @Size(max = 20)
    @Column(name = "mobile_no", length = 20)
    private String mobileNo;

    @Size(max = 20)
    @Column(name = "status", length = 20)
    private String status;


}