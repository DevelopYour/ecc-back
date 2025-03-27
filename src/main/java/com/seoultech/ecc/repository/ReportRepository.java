package com.seoultech.ecc.repository;

import com.seoultech.ecc.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
