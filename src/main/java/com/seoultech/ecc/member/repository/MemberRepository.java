package com.seoultech.ecc.member.repository;

import com.seoultech.ecc.member.datamodel.MemberEntity;
import com.seoultech.ecc.member.datamodel.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Integer> {
    Optional<MemberEntity> findByStudentId(String studentId);

    Optional<MemberEntity> findByEmail(String email);

    boolean existsByStudentId(String studentId);

    List<MemberEntity> findByStatus(MemberStatus status);

    List<MemberEntity> findByLevel(Integer level);

    List<MemberEntity> findByStatusAndLevel(MemberStatus status, Integer level);

    Long countByStatus(MemberStatus status);
}