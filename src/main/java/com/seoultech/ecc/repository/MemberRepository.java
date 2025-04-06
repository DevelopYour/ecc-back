package com.seoultech.ecc.repository;

import com.seoultech.ecc.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Integer> {
    Optional<MemberEntity> findByStudentId(String studentId);
    Optional<MemberEntity> findByEmail(String email);
}
