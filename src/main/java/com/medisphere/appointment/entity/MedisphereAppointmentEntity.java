package com.medisphere.appointment.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "medisphere_appointment")
public class MedisphereAppointmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient", nullable = false)
    private MedispherePatientEntity patient;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor", nullable = false)
    private DoctorEntity doctor;

    @Column(name = "appointment_date")
    private LocalDate appointmentDate;

    @Column(name = "appointment_time", length = 50)
    private LocalTime appointmentTime;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "reason", length = 250)
    private String reason;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "create_date")
    private LocalDateTime createDate;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @Column(name = "book_reference_id", length = 50)
    private String bookReferenceId;


}