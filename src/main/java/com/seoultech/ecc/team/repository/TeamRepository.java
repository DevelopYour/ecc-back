package com.seoultech.ecc.team.repository;

import com.seoultech.ecc.team.datamodel.TeamEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamRepository extends JpaRepository<TeamEntity, Integer> { // Long -> Integer 변경

    // 정규/번개 스터디 조회
    List<TeamEntity> findByIsRegular(boolean isRegular, Sort sort);

    // 생성자로 팀 조회 - @Query 제거하고 JPA 네이밍 컨벤션 적용
    List<TeamEntity> findByCreatedByOrderByCreatedAtDesc(Integer createdBy);

    // 팀원으로 팀 조회 - 복잡한 조인이 필요하므로 @Query 유지하되 타입 변경
    @Query("SELECT t FROM TeamEntity t JOIN t.teamMembers tm JOIN tm.member m " +
            "WHERE m.uuid = :uuid ORDER BY t.createdAt DESC")
    List<TeamEntity> findTeamsByMemberUuid(@Param("uuid") Integer uuid);

    // 년도, 학기로 팀 조회
    List<TeamEntity> findByYearAndSemesterOrderByCreatedAtDesc(int year, int semester);
}