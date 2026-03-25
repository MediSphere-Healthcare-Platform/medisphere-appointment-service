package com.medisphere.appointment.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "doctor_table")
public class DoctorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dr_id", nullable = false)
    private Integer id;

    @Size(max = 200)
    @NotNull
    @Column(name = "dr_name", nullable = false, length = 200)
    private String drName;

    @Size(max = 50)
    @NotNull
    @Column(name = "specialty", nullable = false, length = 50)
    private String specialty;

    @Size(max = 800)
    @NotNull
    @Column(name = "dr_licence", nullable = false, length = 800)
    private String drLicence;

    @Size(max = 15)
    @Column(name = "dr_contact_no", length = 15)
    private String drContactNo;

    @Size(max = 20)
    @NotNull
    @Column(name = "dr_nic", nullable = false, length = 20)
    private String drNic;

    @Size(max = 20)
    @NotNull
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @CreationTimestamp
    @Column(name = "create_date")
    private LocalDateTime createDate;

    @UpdateTimestamp
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;


}