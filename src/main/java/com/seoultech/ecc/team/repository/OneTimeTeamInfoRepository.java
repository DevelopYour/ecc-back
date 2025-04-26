package com.seoultech.ecc.team.repository;

import com.seoultech.ecc.team.datamodel.OneTimeTeamInfoEntity;
import com.seoultech.ecc.team.datamodel.OneTimeTeamStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OneTimeTeamInfoRepository extends JpaRepository<OneTimeTeamInfoEntity, Long> {
    List<OneTimeTeamInfoEntity> findByStatus(OneTimeTeamStatus status, Sort sort);

    List<OneTimeTeamInfoEntity> findByStatusIn(List<OneTimeTeamStatus> statuses, Sort sort);

    List<OneTimeTeamInfoEntity> findByStatusNot(OneTimeTeamStatus status);

    @Query("SELECT o FROM OneTimeTeamInfoEntity o WHERE o.startTime > :now ORDER BY o.startTime ASC")
    List<OneTimeTeamInfoEntity> findUpcomingOneTimeTeams(@Param("now") LocalDateTime now, Sort sort);

    @Query("SELECT o FROM OneTimeTeamInfoEntity o JOIN o.team t WHERE t.createdBy = :studentId")
    List<OneTimeTeamInfoEntity> findOneTimeTeamsByCreator(@Param("studentId") String studentId, Sort sort);

    @Query("SELECT o FROM OneTimeTeamInfoEntity o JOIN o.team t JOIN t.teamMembers tm JOIN tm.member m " +
            "WHERE m.studentId = :studentId")
    List<OneTimeTeamInfoEntity> findOneTimeTeamsByMember(@Param("studentId") String studentId, Sort sort);

    /**
     * 지정된 날짜 이전에 취소된 번개 스터디 목록 조회
     */
    @Query("SELECT o FROM OneTimeTeamInfoEntity o WHERE o.status = :status AND o.canceledAt < :threshold")
    List<OneTimeTeamInfoEntity> findByCanceledBeforeAndStatus(
            @Param("threshold") LocalDateTime threshold,
            @Param("status") OneTimeTeamStatus status);
}