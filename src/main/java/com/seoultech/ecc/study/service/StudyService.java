package com.seoultech.ecc.study.service;

import com.seoultech.ecc.expression.ExpressionDto;
import com.seoultech.ecc.report.datamodel.ReportEntity;
import com.seoultech.ecc.report.dto.ReportDto;
import com.seoultech.ecc.report.service.ReportService;
import com.seoultech.ecc.study.datamodel.StudyStatus;
import com.seoultech.ecc.study.dto.StudyDto;
import com.seoultech.ecc.study.dto.StudySummaryDto;
import com.seoultech.ecc.study.dto.WeeklySummaryDto;
import com.seoultech.ecc.study.repository.StudyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudyService {

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private ReportService reportService;

    @Autowired
    private ReviewService reviewService;

    public List<WeeklySummaryDto> getTeamProgress(Long teamId) {
        List<ReportEntity> reports = reportService.findReportsByTeamId(teamId);
        List<WeeklySummaryDto> dtos = new ArrayList<>();
        List<StudySummaryDto> studyList = new ArrayList<>();
        for (ReportEntity report : reports) {
            WeeklySummaryDto dto = new WeeklySummaryDto();
            StudySummaryDto studyDto = new StudySummaryDto();
            studyDto.setTeamId(teamId);
            studyDto.setWeek(report.getWeek());
            // TODO: 해당 reportId를 갖는 REDIS HASH 존재 시 ONGOING으로 처리 로직 추가하기
            if(report.isSubmitted()){ // 보고서 제출 완료
                studyDto.setStudyStatus(StudyStatus.COMPLETE);
                dto.setReviewSummaries(reviewService.getReviewStatusInfos(report.getReportId()));
            } else { // 보고서 미제출
                studyDto.setStudyStatus(StudyStatus.DRAFTING);
                dto.setReviewSummaries(null);
            }
            dto.setStudySummary(studyDto);
            dtos.add(dto);
        }
        return dtos;
    }

    public Long createStudyRoom(Long teamId) {
        // 보고서 초안 생성 후 보고서 아이디 받기
        Long reportId = reportService.createReport(teamId);
        // TODO: 공부방 생성 로직 추가 (REDIS의 STUDY HASH). 아이디를 보고서 아이디와 동일하게 지정하기
        return reportId;
    }

    public void submitReport(Long reportId, List<ExpressionDto> expressions) {
        ReportEntity report = reportService.findByReportId(reportId);
        // TODO: Expressions 저장
        StringBuilder contents = new StringBuilder();
        for(ExpressionDto expression : expressions){
            contents.append(expression.getExpression()).append(": ").append(expression.getDescription()).append("\n");
        }
        report.setContents(contents.toString());
        report.setSubmitted(true);
        reportService.saveReport(report);
    }

    public ReportEntity getReport(Long reportId) {
        return reportService.findByReportId(reportId);
    }
}
