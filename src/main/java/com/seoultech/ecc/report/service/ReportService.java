package com.seoultech.ecc.report.service;

import com.seoultech.ecc.member.datamodel.MemberEntity;
import com.seoultech.ecc.report.datamodel.ReportEntity;
import com.seoultech.ecc.report.datamodel.ReportMemberEntity;
import com.seoultech.ecc.report.dto.ReportResponseDto;
import com.seoultech.ecc.report.repository.ReportRepository;
import com.seoultech.ecc.team.datamodel.SubjectEntity;
import com.seoultech.ecc.team.datamodel.TeamEntity;
import com.seoultech.ecc.team.repository.SubjectRepository;
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
    private TeamRepository teamRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    // String → Integer 변경
    public List<ReportResponseDto> findReportsByTeamId(Integer teamId) {
        List<ReportEntity> entities = reportRepository.findByTeamTeamIdOrderByWeekAsc(teamId);
        return entities.stream()
                .map(ReportResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    // String → Integer 변경, parseInt 제거
    public ReportResponseDto findByReportId(Integer reportId) {
        ReportEntity entity = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found with id: " + reportId));
        return ReportResponseDto.fromEntity(entity);
    }

    // String → Integer 변경, parseInt 제거
    public ReportEntity findEntityByReportId(Integer reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found with id: " + reportId));
    }

    // 반환 타입 String → Integer 변경
    public Integer saveReport(ReportEntity reportEntity) {
        ReportEntity saved = reportRepository.save(reportEntity);
        return saved.getId();
    }

    // String → Integer 변경
    public boolean checkReportSubmitStatus(Integer reportId) {
        ReportEntity entity = findEntityByReportId(reportId);
        return entity.isSubmitted();
    }

    @Transactional
    public Integer createReport(Integer teamId) { // Long → Integer 변경
        TeamEntity team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found with id: " + teamId));

        // 기본 Subject 조회 (TODO: 추후 처리 필요)
        SubjectEntity subject = subjectRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Default subject not found"));

        ReportEntity entity = new ReportEntity();
        entity.setTeam(team);
        entity.setSubject(subject);
        entity.setWeek(team.getStudyCount() + 1);
        entity.setGrade(0);
        entity.setSubmitted(false);
        entity.setContents(""); // TODO: AI

        // 팀 멤버 정보 추가
        List<MemberEntity> memberEntities = teamMemberRepository.findMembersByTeamId(teamId);
        List<ReportMemberEntity> reportMembers = new ArrayList<>();

        for (MemberEntity member : memberEntities) {
            ReportMemberEntity reportMember = new ReportMemberEntity();
            reportMember.setReport(entity);
            reportMember.setMember(member);
            reportMembers.add(reportMember);
        }
        entity.setReportMembers(reportMembers);

        // 팀의 스터디 카운트 증가
        team.setStudyCount(team.getStudyCount() + 1);
        teamRepository.save(team);

        ReportEntity saved = reportRepository.save(entity);
        return saved.getId(); // Integer 반환
    }
}