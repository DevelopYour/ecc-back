package com.seoultech.ecc.team.repository;

import com.seoultech.ecc.member.datamodel.MemberEntity;
import com.seoultech.ecc.team.datamodel.TeamMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMemberEntity, Integer> { // Long -> Integer 변경

    // 복잡한 조인 쿼리는 @Query 유지하되 파라미터 타입 변경
    @Query("SELECT tm.member FROM TeamMemberEntity tm WHERE tm.team.teamId = :teamId")
    List<MemberEntity> findMembersByTeamId(@Param("teamId") Integer teamId);
}