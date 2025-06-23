package com.seoultech.ecc.team.repository;

import com.seoultech.ecc.team.datamodel.OneTimeTeamInfoEntity;
import com.seoultech.ecc.team.datamodel.OneTimeTeamStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OneTimeTeamInfoRepository extends JpaRepository<OneTimeTeamInfoEntity, Integer> { // Long -> Integer 변경

    List<OneTimeTeamInfoEntity> findByStatus(OneTimeTeamStatus status, Sort sort);

    List<OneTimeTeamInfoEntity> findByStatusIn(List<OneTimeTeamStatus> statuses, Sort sort);

    List<OneTimeTeamInfoEntity> findByStatusNot(OneTimeTeamStatus status);

    @Query("SELECT o FROM OneTimeTeamInfoEntity o WHERE o.startTime > :now ORDER BY o.startTime ASC")
    List<OneTimeTeamInfoEntity> findUpcomingOneTimeTeams(@Param("now") LocalDateTime now, Sort sort);

    // JPA 네이밍 컨벤션으로 변경 가능한 부분은 변경
    List<OneTimeTeamInfoEntity> findByTeamCreatedByOrderByCreatedAtDesc(Integer createdBy);

    @Query("SELECT o FROM OneTimeTeamInfoEntity o JOIN o.team t JOIN t.teamMembers tm JOIN tm.member m " +
            "WHERE m.uuid = :uuid ORDER BY o.createdAt DESC")
    List<OneTimeTeamInfoEntity> findOneTimeTeamsByMemberUuid(@Param("uuid") Integer uuid);

    /**
     * 지정된 날짜 이전에 취소된 번개 스터디 목록 조회
     */
    List<OneTimeTeamInfoEntity> findByStatusAndCanceledAtBefore(OneTimeTeamStatus status, LocalDateTime threshold);
}