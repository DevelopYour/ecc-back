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

    public List<ReportResponseDto> findReportsByTeamId(Long teamId) {
        List<ReportEntity> entities = reportRepository.findByTeamIdOrderByWeekAsc(teamId);
        return entities.stream()
                .map(ReportResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public ReportResponseDto findByReportId(String reportId) {
        try {
            Integer id = Integer.parseInt(reportId);
            ReportEntity entity = reportRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Report not found with id: " + reportId));
            return ReportResponseDto.fromEntity(entity);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid report ID format: " + reportId);
        }
    }

    public ReportEntity findEntityByReportId(String reportId) {
        try {
            Integer id = Integer.parseInt(reportId);
            return reportRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Report not found with id: " + reportId));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid report ID format: " + reportId);
        }
    }

    public String saveReport(ReportEntity reportEntity) {
        ReportEntity saved = reportRepository.save(reportEntity);
        return saved.getId().toString();
    }

    // 보고서 제출 여부 확인
    public boolean checkReportSubmitStatus(String reportId) {
        ReportEntity entity = findEntityByReportId(reportId);
        return entity.isSubmitted();
    }

    @Transactional
    public String createReport(Long teamId) {
        TeamEntity team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found with id: " + teamId));

        // 기본 Subject 조회 (TODO: 추후 처리 필요)
        SubjectEntity subject = subjectRepository.findById(1L)
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
        return saved.getId().toString();
    }

    // 호환성을 위한 메서드들 (기존 Document 형태의 응답을 유지)
    public ReportResponseDto findByReportIdAsDocument(String reportId) {
        return findByReportId(reportId);
    }

    public String saveReportAsDocument(ReportResponseDto reportDto) {
        ReportEntity entity = findEntityByReportId(reportDto.getId());

        // DTO의 변경사항을 Entity에 반영
        entity.setContents(reportDto.getContents());
        entity.setGrade(reportDto.getGrade());
        entity.setSubmitted(reportDto.isSubmitted());

        return saveReport(entity);
    }
}