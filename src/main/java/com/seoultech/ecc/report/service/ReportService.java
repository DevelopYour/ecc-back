package com.seoultech.ecc.report.service;

import com.seoultech.ecc.report.datamodel.ReportEntity;
import com.seoultech.ecc.report.repository.ReportRepository;
import com.seoultech.ecc.team.datamodel.TeamEntity;
import com.seoultech.ecc.team.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Long saveReport(ReportEntity reportEntity) {
        return reportRepository.save(reportEntity).getReportId();
    }

    // 보고서 제출 여부 확인
    public boolean checkReportSubmitStatus(Long reportId) {
        return reportRepository.findByReportId(reportId).isSubmitted();
    }

    @Transactional
    public Long createReport(Long teamId) {
        TeamEntity team = teamRepository.findById(teamId).orElse(null); // TODO: 추후 처리 필요
        ReportEntity entity = new ReportEntity();
        entity.setTeamId(teamId);
        entity.setSubjectId(1L); // TODO: 추후 처리 필요
        entity.setWeek(team.getStudyCount() + 1);
        team.setStudyCount(team.getStudyCount() + 1);
        entity.setGrade(0);
        entity.setSubmitted(false);
        entity.setContents(""); //TODO: AI
        return reportRepository.save(entity).getReportId();
    }
}
