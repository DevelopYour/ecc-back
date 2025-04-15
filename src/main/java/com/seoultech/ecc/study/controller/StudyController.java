package com.seoultech.ecc.study.controller;

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

    @GetMapping("/{teamId}")
    @Operation(summary = "팀별 공부방 메인페이지 입장", description = "팀의 주차별 진행과정을 조회합니다. StudyStatus가 COMPLETE인 경우 팀원별 복습 상태 정보를 함께 반환합니다")
    public ResponseEntity<List<WeeklySummaryDto>> summarizeTeamProgress(@PathVariable Long teamId) {
        return ResponseEntity.ok(studyService.getTeamProgress(teamId));
    }

//    @GetMapping("/{teamId}/{week}")
//    @Operation(summary = "주차별 공부방 입장", description = "팀 ID와 주차 정보를 기반으로 공부방에 입장합니다.")
//    public ResponseEntity<StudyRoomDto> enterWeeklyStudyRoom(@PathVariable Long teamId, @PathVariable int week) {
//        return ResponseEntity.ok(studyService.enterWeeklyStudyRoom(teamId, week));
//    }
//
//    @PostMapping("/{teamId}/{week}")
//    @Operation(summary = "공부방 생성", description = "특정 팀의 특정 주차 공부방을 생성합니다.")
//    public ResponseEntity<Void> createStudyRoom(@PathVariable Long teamId, @PathVariable int week) {
//        studyService.createStudyRoom(teamId, week);
//        return ResponseEntity.ok().build();
//    }
//
//    @GetMapping("/{teamId}/{week}/guide")
//    @Operation(summary = "콘텐츠 가이드라인 조회", description = "해당 주차의 콘텐츠 가이드라인을 조회합니다.")
//    public ResponseEntity<GuideDto> getStudyGuide(@PathVariable Long teamId, @PathVariable int week) {
//        return ResponseEntity.ok(studyService.getGuide(teamId, week));
//    }
//
//    @PostMapping("/{teamId}/{week}/ai-help")
//    @Operation(summary = "AI 도움 받기", description = "AI에게 표현의 의미나 예문을 요청합니다.")
//    public ResponseEntity<AiHelpResponseDto> getAiHelp(@PathVariable Long teamId, @PathVariable int week,
//                                                       @RequestBody AiHelpRequestDto request) {
//        return ResponseEntity.ok(studyService.getAiHelp(teamId, week, request));
//    }
//
//    @GetMapping("/{teamId}/{week}/report")
//    @Operation(summary = "보고서 조회", description = "보고서를 조회합니다.")
//    public ResponseEntity<ReportDto> getReport(@PathVariable Long teamId, @PathVariable int week) {
//        return ResponseEntity.ok(studyService.getReport(teamId, week));
//    }
//
//    @PostMapping("/{teamId}/{week}/report")
//    @Operation(summary = "보고서 초안 생성", description = "보고서 초안을 생성합니다.")
//    public ResponseEntity<Void> createReportDraft(@PathVariable Long teamId, @PathVariable int week,
//                                                  @RequestBody ReportDraftDto draftDto) {
//        studyService.createReportDraft(teamId, week, draftDto);
//        return ResponseEntity.ok().build();
//    }
//
//    @PatchMapping("/{teamId}/{week}/report")
//    @Operation(summary = "보고서 제출", description = "최종 보고서를 제출합니다.")
//    public ResponseEntity<Void> submitReport(@PathVariable Long teamId, @PathVariable int week,
//                                             @RequestBody ReportSubmitDto submitDto) {
//        studyService.submitReport(teamId, week, submitDto);
//        return ResponseEntity.ok().build();
//    }
}

