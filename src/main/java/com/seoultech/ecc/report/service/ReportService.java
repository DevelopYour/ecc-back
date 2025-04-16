package com.seoultech.ecc.report.service;

import com.seoultech.ecc.report.datamodel.ReportDocument;
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

    public List<ReportDocument> findReportsByTeamId(Long teamId) {
        return reportRepository.findByTeamIdOrderByWeekAsc(teamId);
    }

    public ReportDocument findByReportId(String reportId) {
        return reportRepository.getById(reportId);
    }

    public String saveReport(ReportDocument reportEntity) {
        return reportRepository.save(reportEntity).getId();
    }

    // 보고서 제출 여부 확인
    public boolean checkReportSubmitStatus(String reportId) {
        return reportRepository.findById(reportId).get().isSubmitted();
    }

    @Transactional
    public String createReport(Long teamId) {
        TeamEntity team = teamRepository.findById(teamId).orElse(null); // TODO: 추후 처리 필요
        ReportDocument entity = new ReportDocument();
        entity.setTeamId(teamId);
        entity.setSubjectId(1L); // TODO: 추후 처리 필요
        entity.setWeek(team.getStudyCount() + 1);
        team.setStudyCount(team.getStudyCount() + 1);
        entity.setGrade(0);
        entity.setSubmitted(false);
        entity.setContents(""); //TODO: AI
        return reportRepository.save(entity).getId();
    }
}
