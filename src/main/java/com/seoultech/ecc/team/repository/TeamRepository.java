package com.seoultech.ecc.team.repository;

import com.seoultech.ecc.team.datamodel.TeamEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamRepository extends JpaRepository<TeamEntity, Integer> {
    // 정규/번개 스터디 조회
    List<TeamEntity> findByRegular(boolean regular, Sort sort);

    // 팀원으로 팀 조회
    @Query("SELECT t FROM TeamEntity t JOIN t.teamMembers tm JOIN tm.member m " +
            "WHERE m.id = :uuid")
    List<TeamEntity> findTeamsByMember(@Param("uuid") Integer uuid, Sort sort);

    // 년도, 학기로 팀 조회
    List<TeamEntity> findBySemester_Id(Integer semesterId);

    Long countByRegular(boolean isRegular);
}