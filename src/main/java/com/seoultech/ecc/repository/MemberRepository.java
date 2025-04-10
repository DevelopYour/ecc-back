package com.seoultech.ecc.repository;

import com.seoultech.ecc.entity.MemberEntity;
import com.seoultech.ecc.entity.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Integer> {
    Optional<MemberEntity> findByStudentId(String studentId);

    Optional<MemberEntity> findByEmail(String email);

    boolean existsByStudentId(String studentId);

    List<MemberEntity> findByStatus(MemberStatus status);
}