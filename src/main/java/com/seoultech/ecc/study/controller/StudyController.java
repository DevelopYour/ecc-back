package com.seoultech.ecc.study.controller;

import com.seoultech.ecc.member.dto.ResponseDto;
import com.seoultech.ecc.report.dto.ReportResponseDto;
import com.seoultech.ecc.study.datamodel.StudyRedis;
import com.seoultech.ecc.study.dto.*;
import com.seoultech.ecc.study.service.StudyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/study")
@RequiredArgsConstructor
@Tag(name = "스터디 API", description = "팀별 스터디 진행 관련 API")
public class StudyController {

    @Autowired
    private StudyService studyService;

    @GetMapping("/{teamId}/overview")
    @Operation(summary = "팀별 메인페이지 입장", description = "팀의 주차별 진행과정을 조회합니다. StudyStatus가 COMPLETE인 경우 팀원별 복습 상태 정보를 함께 반환합니다")
    public ResponseEntity<ResponseDto<List<WeeklySummaryDto>>> summarizeTeamProgress(@PathVariable Integer teamId) { // Long → Integer 변경
        List<WeeklySummaryDto> progress = studyService.getTeamProgress(teamId);
        return ResponseEntity.ok(ResponseDto.success(progress));
    }

    @PostMapping("/{teamId}")
    @Operation(summary = "공부방 입장", description = "진행 중인 공부방이 없다면 특정 팀의 특정 주차 보고서 초안 데이터를 생성하고 공부방(Redis)을 생성합니다.")
    public ResponseEntity<ResponseDto<StudyRedis>> enterStudyRoom(@PathVariable Integer teamId) { // Long → Integer 변경
        StudyRedis studyRoom = studyService.getStudyRoom(teamId);
        return ResponseEntity.ok(ResponseDto.success(studyRoom));
    }

    @GetMapping("/{teamId}/topic")
    @Operation(summary = "추천 주제 목록 조회", description = "추천 주제 목록을 요청합니다.")
    public ResponseEntity<ResponseDto<List<TopicRecommendationDto>>> getTopicsByAiHelp(@PathVariable Integer teamId) { // Long → Integer 변경
        List<TopicRecommendationDto> topics = studyService.getTopicRecommendations(teamId);
        return ResponseEntity.ok(ResponseDto.success(topics));
    }

    @PostMapping("/{studyId}/topic")
    @Operation(summary = "주제 선정", description = "주제 목록을 저장합니다.")
    public ResponseEntity<ResponseDto<StudyRedis>> saveTopics(@PathVariable String studyId, @RequestBody List<TopicDto> topics) {
        StudyRedis result = studyService.addTopicToStudy(studyId, topics);
        return ResponseEntity.ok(ResponseDto.success(result));
    }

    @PostMapping("/{studyId}/ai-help")
    @Operation(summary = "AI 도움 받기", description = "AI에게 표현 관련 질문 후 해당 데이터를 저장합니다.")
    public ResponseEntity<ResponseDto<StudyRedis>> getExpressionByAiHelp(@PathVariable String studyId, @RequestBody ExpressionToAskDto question) {
        StudyRedis result = studyService.getAiHelpAndAdd(studyId, question);
        return ResponseEntity.ok(ResponseDto.success(result));
    }

    @PutMapping("/{studyId}")
    @Operation(summary = "스터디 종료", description = "StudyRedis의 데이터를 ReportDocument로 옮긴 뒤 삭제합니다.")
    public ResponseEntity<ResponseDto<String>> finishStudy(@PathVariable String studyId) {
        String result = studyService.finishStudy(studyId);
        return ResponseEntity.ok(ResponseDto.success(result));
    }

    @GetMapping("/report/{reportId}")
    @Operation(summary = "보고서 조회", description = "보고서를 조회합니다.")
    public ResponseEntity<ResponseDto<ReportResponseDto>> getReport(@PathVariable Integer reportId) { // String → Integer 변경
        ReportResponseDto report = studyService.getReport(reportId);
        return ResponseEntity.ok(ResponseDto.success(report));
    }

    @PatchMapping("/report/{reportId}")
    @Operation(summary = "보고서 제출", description = "최종 보고서를 제출합니다.")
    public ResponseEntity<ResponseDto<String>> submitReport(@PathVariable Integer reportId) { // String → Integer 변경
        String result = studyService.submitReportAndCreateReview(reportId);
        return ResponseEntity.ok(ResponseDto.success(result));
    }
}