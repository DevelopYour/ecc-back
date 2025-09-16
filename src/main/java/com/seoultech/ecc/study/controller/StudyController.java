package com.seoultech.ecc.study.controller;

import com.seoultech.ecc.member.dto.ResponseDto;
import com.seoultech.ecc.member.dto.CustomUserDetails;
import com.seoultech.ecc.report.datamodel.ReportDocument;
import com.seoultech.ecc.study.datamodel.CorrectionRedis;
import com.seoultech.ecc.study.datamodel.StudyRedis;
import com.seoultech.ecc.study.datamodel.VocabRedis;
import com.seoultech.ecc.study.dto.*;
import com.seoultech.ecc.study.service.StudyService;
import com.seoultech.ecc.study.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/study")
@RequiredArgsConstructor
@Tag(name = "스터디 API", description = "팀별 스터디 진행 관련 API")
@SecurityRequirement(name = "Bearer Authentication")
public class StudyController {

    private final StudyService studyService;
    private final TopicService topicService;

    @GetMapping("/team/{teamId}/overview")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "팀별 메인페이지 입장", description = "팀의 주차별 진행과정을 조회합니다. StudyStatus가 COMPLETE인 경우 팀원별 복습 상태 정보를 함께 반환합니다.")
    public ResponseEntity<ResponseDto<List<WeeklySummaryDto>>> summarizeTeamProgress(
            @PathVariable Integer teamId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ResponseDto.success(studyService.getTeamProgress(teamId)));
    }

    @PostMapping("/team/{teamId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "공부방 입장", description = "진행 중인 공부방이 없다면 특정 팀의 특정 주차 보고서 초안 데이터를 생성하고 공부방(Redis)을 생성 후 아이디를 반환합니다.")
    public ResponseEntity<ResponseDto<StudyEnterDto>> enterSpeakingStudyRoom(
            @PathVariable Integer teamId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ResponseDto.success(studyService.getStudyRoom(teamId)));
    }

    @GetMapping("/{studyId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "공부방 데이터 조회", description = "Study Redis의 id를 통해 해당 데이터를 조회합니다.")
    public ResponseEntity<ResponseDto<StudyRedis>> getStudyRedis(
            @PathVariable String studyId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ResponseDto.success(studyService.getStudyData(studyId)));
    }

    @GetMapping("/team/{teamId}/topic")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "주제 목록 조회", description = "주제 목록을 요청합니다.")
    public ResponseEntity<ResponseDto<List<TopicSetDto>>> getTopicsByAiHelp(
            @PathVariable Integer teamId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ResponseDto.success(topicService.getAllTopics()));
    }

    @PostMapping("/{studyId}/topic")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "주제 선정", description = "주제 목록을 저장합니다.")
    public ResponseEntity<ResponseDto<StudyRedis>> saveTopics(
            @PathVariable String studyId,
            @RequestBody List<TopicDto> topics,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ResponseDto.success(studyService.addTopicToStudy(studyId, topics)));
    }

    @PostMapping("/{studyId}/ai-help")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "AI 도움 받기 (회화)", description = "회화 주제에 대해 AI에게 표현 관련 질문 후 해당 데이터를 저장합니다.")
    public ResponseEntity<ResponseDto<StudyRedis>> getExpressionByAiHelp(
            @PathVariable String studyId,
            @RequestBody ExpressionToAskDto question,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ResponseDto.success(studyService.getAiHelpAndAdd(studyId, question)));
    }

    @PostMapping("/{studyId}/general/corrections")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "오답 저장 (일반 과목)", description = "일반 과목의 오답 정리 데이터를 저장합니다.")
    public ResponseEntity<ResponseDto<StudyRedis>> saveCorrections(
            @PathVariable String studyId,
            @RequestBody List<CorrectionRedis> corrections,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ResponseDto.success(studyService.addCorrectionsToGeneralStudy(studyId, corrections)));
    }

    @PostMapping("/{studyId}/general/vocabs")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "단어 저장 (일반 과목)", description = "일반 과목의 단어 정리 데이터를 저장합니다.")
    public ResponseEntity<ResponseDto<StudyRedis>> saveVocabs(
            @PathVariable String studyId,
            @RequestBody List<VocabRedis> vocabs,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ResponseDto.success(studyService.addVocabsToGeneralStudy(studyId, vocabs)));
    }

    @PostMapping("/{studyId}/general/ai-help")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "AI 도움 받기 (일반 과목)", description = "일반 과목에서 AI에게 표현 관련 질문 후 해당 데이터를 저장합니다.")
    public ResponseEntity<ResponseDto<StudyRedis>> getGeneralExpressionByAiHelp(
            @PathVariable String studyId,
            @RequestBody ExpressionToAskDto question,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ResponseDto.success(studyService.getGeneralAiHelpAndAdd(studyId, question)));
    }

    @PutMapping("/{studyId}/general")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "일반 과목 스터디 종료", description = "StudyRedis의 데이터를 ReportDocument로 옮긴 뒤 삭제합니다.")
    public ResponseEntity<ResponseDto<String>> finishGeneralStudy(
            @PathVariable String studyId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ResponseDto.success(studyService.finishGeneralStudy(studyId)));
    }

    @PutMapping("/{studyId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "스터디 종료", description = "StudyRedis의 데이터를 ReportDocument로 옮긴 뒤 삭제합니다.")
    public ResponseEntity<ResponseDto<String>> finishStudy(
            @PathVariable String studyId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ResponseDto.success(studyService.finishStudy(studyId)));
    }

    @GetMapping("/report/{reportId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "보고서 조회", description = "보고서를 조회합니다.")
    public ResponseEntity<ResponseDto<ReportDocument>> getReport(
            @PathVariable String reportId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ResponseDto.success(studyService.getReport(reportId)));
    }

    @PatchMapping("/report/{reportId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "보고서 제출", description = "최종 보고서를 제출합니다.")
    public ResponseEntity<ResponseDto<String>> submitReport(
            @PathVariable String reportId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ResponseDto.success(studyService.submitReportAndCreateReview(reportId)));
    }
}