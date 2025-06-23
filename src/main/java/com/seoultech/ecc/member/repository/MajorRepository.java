package com.seoultech.ecc.member.repository;

import com.seoultech.ecc.member.datamodel.MajorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MajorRepository extends JpaRepository<MajorEntity, Integer> { // Long -> Integer 변경
    Optional<MajorEntity> findByName(String name);
}