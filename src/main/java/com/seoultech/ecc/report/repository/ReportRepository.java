package com.seoultech.ecc.report.repository;

import com.seoultech.ecc.report.datamodel.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<ReportEntity, Integer> {

    // @Query 제거하고 JPA 네이밍 컨벤션 적용
    List<ReportEntity> findByTeamTeamIdOrderByWeekAsc(Integer teamId);

    Optional<ReportEntity> findByTeamTeamIdAndWeek(Integer teamId, int week);

    // 복잡한 조인이 필요한 경우만 @Query 유지
    @Query("SELECT r FROM ReportEntity r WHERE r.team.year = :year AND r.team.semester = :semester AND r.team.isRegular = true")
    List<ReportEntity> findByYearAndSemesterAndRegular(@Param("year") int year, @Param("semester") int semester);

    List<ReportEntity> findByIsSubmitted(boolean isSubmitted);
}