package com.medisphere.appointment.repository;

import com.medisphere.appointment.entity.CommonUrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommonUrlRepository extends JpaRepository<CommonUrlEntity, Long> {
    Optional<CommonUrlEntity> findByCode(String code);
}
