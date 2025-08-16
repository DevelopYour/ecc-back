package com.seoultech.ecc.report.service;

import com.seoultech.ecc.member.datamodel.MemberEntity;
import com.seoultech.ecc.member.dto.MemberSimpleDto;
import com.seoultech.ecc.report.datamodel.ReportDocument;
import com.seoultech.ecc.report.repository.ReportRepository;
import com.seoultech.ecc.team.datamodel.TeamEntity;
import com.seoultech.ecc.team.repository.TeamMemberRepository;
import com.seoultech.ecc.team.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {
    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private TeamRepository teamRepository; // TODO: 사용 로직 TeamService로 분리

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    public List<ReportDocument> findReportsByTeamId(Integer teamId) {
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
    public String createReport(Integer teamId) {
        TeamEntity team = teamRepository.findById(teamId).orElse(null); // TODO: 추후 처리 필요
        ReportDocument report = new ReportDocument();
        report.setTeamId(teamId);
        List<MemberSimpleDto> members = teamMemberRepository.findMembersByTeamId(teamId).stream().map(MemberSimpleDto::fromEntity).collect(Collectors.toList());
        report.setMembers(members);
        report.setSubjectId(1); // TODO: 추후 처리 필요
        report.setWeek(team.getStudyCount() + 1);
        team.setStudyCount(team.getStudyCount() + 1);
        report.setGrade(null);
        report.setSubmitted(false);
        return reportRepository.save(report).getId();
    }
}
