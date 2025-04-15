package com.seoultech.ecc.report.service;

import com.seoultech.ecc.report.datamodel.ReportEntity;
import com.seoultech.ecc.report.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {
    @Autowired
    private ReportRepository reportRepository;

    public List<ReportEntity> findReportsByTeamId(Long teamId) {
        return reportRepository.findByTeam_TeamIdOrderByWeekAsc(teamId);
    }

    // 보고서 생성 및 제출
    public void saveReport(ReportEntity report) {
        reportRepository.save(report);
    }

    // 보고서 제출 여부 확인
    public boolean checkReportSubmitStatus(Long reportId) {
        return reportRepository.findByReportId(reportId).isSubmitted();
    }
}
