package com.seoultech.ecc.report.repository;

import com.seoultech.ecc.report.datamodel.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<ReportEntity, Integer> {

    @Query("SELECT r FROM ReportEntity r WHERE r.team.teamId = :teamId ORDER BY r.week ASC")
    List<ReportEntity> findByTeamIdOrderByWeekAsc(@Param("teamId") Long teamId);

    @Query("SELECT r FROM ReportEntity r WHERE r.team.teamId = :teamId AND r.week = :week")
    Optional<ReportEntity> findByTeamIdAndWeek(@Param("teamId") Long teamId, @Param("week") int week);

    @Query("SELECT r FROM ReportEntity r WHERE r.team.year = :year AND r.team.semester = :semester AND r.team.isRegular = true")
    List<ReportEntity> findByYearAndSemesterAndRegular(@Param("year") int year, @Param("semester") int semester);

    @Query("SELECT r FROM ReportEntity r WHERE r.isSubmitted = :isSubmitted")
    List<ReportEntity> findByIsSubmitted(@Param("isSubmitted") boolean isSubmitted);
}