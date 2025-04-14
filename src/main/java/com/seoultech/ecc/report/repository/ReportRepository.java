package com.seoultech.ecc.report.repository;

import com.seoultech.ecc.report.datamodel.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<ReportEntity, Long> {
}
