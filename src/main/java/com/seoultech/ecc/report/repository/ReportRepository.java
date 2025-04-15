package com.seoultech.ecc.report.repository;

import com.seoultech.ecc.report.datamodel.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<ReportEntity, Long> {
    List<ReportEntity> findByTeamIdOrderByWeekAsc(Long teamId);
    ReportEntity findByReportId(Long reportId);
}
