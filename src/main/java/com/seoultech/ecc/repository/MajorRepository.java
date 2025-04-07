package com.seoultech.ecc.repository;

import com.seoultech.ecc.entity.MajorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MajorRepository extends JpaRepository<MajorEntity, Long> {
    Optional<MajorEntity> findByName(String name);
}
