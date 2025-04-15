package com.seoultech.ecc.study.controller;

import com.seoultech.ecc.expression.ExpressionDto;
import com.seoultech.ecc.report.datamodel.ReportEntity;
import com.seoultech.ecc.report.dto.ReportDto;
import com.seoultech.ecc.study.dto.*;
import com.seoultech.ecc.study.service.StudyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/study")
@RequiredArgsConstructor
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
    @Operation(summary = "공부방 생성", description = "특정 팀의 특정 주차 보고서 초안 데이터를 생성하고 공부방(Redis)을 생성합니다.")
    public ResponseEntity<Long> createStudyRoom(@PathVariable Long teamId) {
        // TODO: 현재 진행중인 공부방이 있는지 확인하는 로직 추가 (REDIS의 STUDY HASH)
        // 보고서 생성
        Long studyRoomId = studyService.createStudyRoom(teamId);
        return ResponseEntity.ok(studyRoomId);
    }

//    @GetMapping("/{teamId}/{week}")
//    @Operation(summary = "콘텐츠 가이드라인 조회", description = "해당 주차의 콘텐츠 가이드라인을 조회합니다.")
//    public ResponseEntity<GuideDto> getStudyGuide(@PathVariable Long teamId, @PathVariable int week) {
//        return ResponseEntity.ok(studyService.getGuide(teamId, week));
//    }

//    @PostMapping("/{studyId}/ai-help")
//    @Operation(summary = "AI 도움 받기", description = "AI에게 표현의 의미나 예문을 요청합니다.")
//    public ResponseEntity<StudyDto> getAiHelp(@PathVariable Long studyId, @RequestBody AiHelpRequestDto request) {
//        return ResponseEntity.ok(studyService.getAiHelp(studyId, request));
//    }

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

