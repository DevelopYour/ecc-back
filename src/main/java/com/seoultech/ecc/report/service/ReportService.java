package com.seoultech.ecc.report.service;

import com.seoultech.ecc.report.datamodel.ReportEntity;
import com.seoultech.ecc.report.repository.ReportRepository;
import com.seoultech.ecc.team.datamodel.TeamEntity;
import com.seoultech.ecc.team.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {
    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private TeamRepository teamRepository;

    public List<ReportEntity> findReportsByTeamId(Long teamId) {
        return reportRepository.findByTeamIdOrderByWeekAsc(teamId);
    }

    public ReportEntity findByReportId(Long reportId) {
        return reportRepository.findByReportId(reportId);
    }

    public void saveReport(ReportEntity reportEntity) {
        reportRepository.save(reportEntity);
    }

    // 보고서 제출 여부 확인
    public boolean checkReportSubmitStatus(Long reportId) {
        return reportRepository.findByReportId(reportId).isSubmitted();
    }

    public Long createReport(Long teamId) {
        TeamEntity team = teamRepository.findById(teamId).orElse(null); // TODO: 추후 처리 필요
        ReportEntity entity = new ReportEntity();
        entity.setTeamId(teamId);
        entity.setSubmitted(false);
        entity.setWeek(team.getStudyCount() + 1);
        team.setStudyCount(team.getStudyCount() + 1);
//        entity.setContents(); TODO: AI
        return reportRepository.save(entity).getReportId();
    }
}
