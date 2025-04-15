package com.seoultech.ecc.study.service;

import com.seoultech.ecc.report.datamodel.ReportEntity;
import com.seoultech.ecc.report.service.ReportService;
import com.seoultech.ecc.study.datamodel.StudyStatus;
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
}
