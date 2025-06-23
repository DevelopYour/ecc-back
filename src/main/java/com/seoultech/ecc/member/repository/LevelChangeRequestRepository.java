package com.seoultech.ecc.member.repository;

import com.seoultech.ecc.member.datamodel.LevelChangeRequestEntity;
import com.seoultech.ecc.member.datamodel.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LevelChangeRequestRepository extends JpaRepository<LevelChangeRequestEntity, Integer> { // Long -> Integer 변경

    // @Query 제거하고 JPA 네이밍 컨벤션 적용
    List<LevelChangeRequestEntity> findByStatus(LevelChangeRequestEntity.RequestStatus status);

    List<LevelChangeRequestEntity> findByMember(MemberEntity member);

    Optional<LevelChangeRequestEntity> findTopByMemberOrderByCreatedAtDesc(MemberEntity member);

    List<LevelChangeRequestEntity> findByMemberAndStatus(MemberEntity member, LevelChangeRequestEntity.RequestStatus status);
}