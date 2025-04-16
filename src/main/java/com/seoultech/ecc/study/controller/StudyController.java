package com.seoultech.ecc.study.controller;

import com.seoultech.ecc.expression.ExpressionDto;
import com.seoultech.ecc.report.datamodel.ReportEntity;
import com.seoultech.ecc.study.datamodel.redis.StudyRedis;
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
    @Operation(summary = "팀별 공부방 메인페이지 입장", description = "팀의 주차별 진행과정을 조회합니다. StudyStatus가 COMPLETE인 경우 팀원별 복습 상태 정보를 함께 반환합니다")
    public ResponseEntity<List<WeeklySummaryDto>> summarizeTeamProgress(@PathVariable Long teamId) {
        return ResponseEntity.ok(studyService.getTeamProgress(teamId));
    }

//    @GetMapping("/{studyId}")
//    @Operation(summary = "주차별 공부방 입장", description = "팀 ID와 주차 정보를 기반으로 공부방에 입장합니다.")
//    public ResponseEntity<StudyRoomDto> enterWeeklyStudyRoom(@PathVariable Long studyId) {
//        return ResponseEntity.ok(studyService.enterWeeklyStudyRoom(studyId));
//    }

    @PostMapping("/{teamId}")
    @Operation(summary = "공부방 입장", description = "진행 중인 공부방이 없다면 특정 팀의 특정 주차 보고서 초안 데이터를 생성하고 공부방(Redis)을 생성합니다.")
    public ResponseEntity<StudyRedis> enterStudyRoom(@PathVariable Long teamId) {
        return ResponseEntity.ok(studyService.getStudyRoom(teamId));
    }

//    @GetMapping("/{teamId}/{week}")
//    @Operation(summary = "콘텐츠 가이드라인 조회", description = "해당 주차의 콘텐츠 가이드라인을 조회합니다.")
//    public ResponseEntity<GuideDto> getStudyGuide(@PathVariable Long teamId, @PathVariable int week) {
//        return ResponseEntity.ok(studyService.getGuide(teamId, week));
//    }

//    @GetMapping("/{studyId}/topic")
//    @Operation(summary = "주제 선정 - AI 도움 받기", description = "AI에게 추천 주제 목록을 요청합니다.")
//    public ResponseEntity<List<TopicRecommendationDto>> getTopicByAiHelp(@PathVariable Long studyId) {
//        return ResponseEntity.ok(studyService.getTopicRecommendations(studyId));
//    }

    @PostMapping("/{studyId}/topic")
    @Operation(summary = "주제 선정", description = "주제 목록을 저장합니다.")
    public ResponseEntity<StudyRedis> getTopicByAiHelp(@PathVariable Long studyId, @RequestBody List<TopicDto> topics) {
        return ResponseEntity.ok(studyService.addTopicToStudy(studyId, topics));
    }

    @PostMapping("/{studyId}/ai-help")
    @Operation(summary = "AI 도움 받기", description = "AI에게 표현 관련 질문 후 해당 데이터를 저장합니다.")
    public ResponseEntity<StudyRedis> getExpressionByAiHelp(@PathVariable Long studyId, @RequestBody ExpressionToAskDto question) {
        return ResponseEntity.ok(studyService.getAiHelpAndAdd(studyId, question));
    }

    @GetMapping("/report/{reportId}")
    @Operation(summary = "보고서 조회", description = "보고서를 조회합니다.")
    public ResponseEntity<ReportEntity> getReport(@PathVariable Long reportId) {
        return ResponseEntity.ok(studyService.getReport(reportId));
    }

    @PatchMapping("/report/{reportId}")
    @Operation(summary = "보고서 제출", description = "최종 보고서를 제출합니다.")
    public ResponseEntity<Void> submitReport(@PathVariable Long reportId, @RequestBody List<ExpressionDto> expressions) {
        // TODO: StudyDto: Redis의 Study Hash
        studyService.submitReport(reportId, expressions);
        return ResponseEntity.ok().build();
    }
}

