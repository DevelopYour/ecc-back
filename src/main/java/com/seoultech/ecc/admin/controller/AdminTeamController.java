package com.seoultech.ecc.admin.controller;

import com.seoultech.ecc.admin.service.AdminTeamService;
import com.seoultech.ecc.member.dto.CustomUserDetails;
import com.seoultech.ecc.member.dto.ResponseDto;
import com.seoultech.ecc.report.datamodel.ReportDocument;
import com.seoultech.ecc.team.dto.TeamDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/teams")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "팀 관리 API", description = "관리자 전용 팀 관리 API")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminTeamController {

    private final AdminTeamService adminTeamService;

    @GetMapping
    @Operation(summary = "전체 팀 조회", description = "모든 팀 목록을 조회합니다. 정규 스터디와 번개 스터디를 필터링할 수 있습니다.")
    public ResponseEntity<ResponseDto<List<TeamDto>>> getAllTeams(
            @RequestParam(required = false) Boolean regular,
            @RequestParam(required = false) Integer semesterId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Integer uuid = userDetails.getId();

        List<TeamDto> teams = adminTeamService.getAllTeams(uuid, regular, semesterId);
        return ResponseEntity.ok(ResponseDto.success(teams));
    }

    @GetMapping("/{teamId}")
    @Operation(summary = "팀 상세 정보 조회", description = "특정 팀의 상세 정보를 조회합니다. 정규 스터디와 번개 스터디 모두 조회 가능합니다.")
    public ResponseEntity<ResponseDto<TeamDto>> getTeamDetail(
            @PathVariable Integer teamId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Integer uuid = userDetails.getId();

        TeamDto teamDetail = adminTeamService.getTeamDetail(teamId, uuid);
        return ResponseEntity.ok(ResponseDto.success(teamDetail));
    }

    @GetMapping("/{teamId}/{week}")
    @Operation(
            summary = "팀 주차별 상세 정보 조회",
            description = "특정 팀의 주차별 상세 정보를 조회합니다. 정규 스터디만 주차별 조회가 가능합니다."
    )
    public ResponseEntity<ResponseDto<Object>> getTeamWeekDetail(
            @PathVariable Integer teamId,
            @PathVariable int week,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Integer uuid = userDetails.getId();

        try {
            Object weekDetail = adminTeamService.getTeamWeekDetail(teamId, week, uuid);
            return ResponseEntity.ok(ResponseDto.success(weekDetail));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ResponseDto.error(e.getMessage()));
        }
    }

    @GetMapping("/one-time/{teamId}/report")
    @Operation(
            summary = "번개 스터디 보고서 조회",
            description = "번개 스터디의 보고서를 조회합니다. 번개 스터디는 단일 보고서만 존재합니다."
    )
    public ResponseEntity<ResponseDto<ReportDocument>> getOneTimeTeamReport(
            @PathVariable Integer teamId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Integer uuid = userDetails.getId();

        try {
            ReportDocument report = adminTeamService.getOneTimeTeamReport(teamId, uuid);
            return ResponseEntity.ok(ResponseDto.success(report));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ResponseDto.error(e.getMessage()));
        }
    }

    @GetMapping("/{teamId}/{week}/report")
    @Operation(
            summary = "팀 주차별 보고서 조회",
            description = "특정 팀의 주차별 보고서를 조회합니다. 정규 스터디만 주차별 보고서 조회가 가능합니다."
    )
    public ResponseEntity<ResponseDto<ReportDocument>> getTeamWeekReport(
            @PathVariable Integer teamId,
            @PathVariable int week,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Integer uuid = userDetails.getId();

        try {
            ReportDocument report = adminTeamService.getTeamWeekReport(teamId, week, uuid);
            return ResponseEntity.ok(ResponseDto.success(report));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ResponseDto.error(e.getMessage()));
        }
    }

    @PatchMapping("/{teamId}/{week}/report/grade")
    @Operation(
            summary = "정규 스터디 보고서 평가 점수 수정",
            description = "정규 스터디의 주차별 보고서 평가 점수를 수정합니다."
    )
    public ResponseEntity<ResponseDto<ReportDocument>> updateReportGrade(
            @PathVariable Integer teamId,
            @PathVariable int week,
            @RequestParam int grade,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Integer uuid = userDetails.getId();

        try {
            ReportDocument updatedReport = adminTeamService.updateReportGrade(teamId, week, grade, uuid);
            return ResponseEntity.ok(ResponseDto.success("보고서 평가 점수가 수정되었습니다.", updatedReport));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ResponseDto.error(e.getMessage()));
        }
    }

    @DeleteMapping("/one-time/{teamId}")
    @Operation(
            summary = "번개 스터디 삭제",
            description = "번개 스터디를 데이터베이스에서 완전히 삭제합니다. 관리자만 삭제 가능합니다."
    )
    public ResponseEntity<ResponseDto<Void>> deleteOneTimeTeam(
            @PathVariable Integer teamId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Integer uuid = userDetails.getId();

        try {
            adminTeamService.deleteOneTimeTeam(teamId, uuid);
            return ResponseEntity.ok(ResponseDto.success("번개 스터디가 삭제되었습니다.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ResponseDto.error(e.getMessage()));
        }
    }

    @PatchMapping("/{teamId}/score")
    @Operation(
            summary = "팀 점수 수동 조정",
            description = "정규 스터디 팀의 점수를 수동으로 조정합니다. 번개 스터디에는 적용되지 않습니다."
    )
    public ResponseEntity<ResponseDto<TeamDto>> updateTeamScore(
            @PathVariable Integer teamId,
            @RequestParam int score,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Integer uuid = userDetails.getId();

        try {
            TeamDto updatedTeam = adminTeamService.updateTeamScore(teamId, score, uuid);
            return ResponseEntity.ok(ResponseDto.success("팀 점수가 수정되었습니다.", updatedTeam));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ResponseDto.error(e.getMessage()));
        }
    }

    @GetMapping("/{teamId}/members")
    @Operation(
            summary = "팀 멤버 조회",
            description = "특정 팀의 멤버 목록을 조회합니다. 정규 스터디와 번개 스터디 모두 조회 가능합니다."
    )
    public ResponseEntity<ResponseDto<Object>> getTeamMembers(
            @PathVariable Integer teamId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Integer uuid = userDetails.getId();

        Object members = adminTeamService.getTeamMembers(teamId, uuid);
        return ResponseEntity.ok(ResponseDto.success(members));
    }

    @PostMapping("/{teamId}/members")
    @Operation(
            summary = "팀 멤버 추가",
            description = "특정 팀에 멤버를 추가합니다. 정규 스터디와 번개 스터디 모두 적용 가능합니다."
    )
    public ResponseEntity<ResponseDto<Object>> addTeamMember(
            @PathVariable Integer teamId,
            @RequestParam Integer memberUuid, // studentId 대신 uuid 사용
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Integer adminUuid = userDetails.getId();

        try {
            Object result = adminTeamService.addTeamMember(teamId, memberUuid, adminUuid);
            return ResponseEntity.ok(ResponseDto.success("팀원이 추가되었습니다.", result));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.ok(ResponseDto.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{teamId}/members/{memberUuid}")
    @Operation(
            summary = "팀 멤버 삭제",
            description = "특정 팀에서 멤버를 삭제합니다. 정규 스터디와 번개 스터디 모두 적용 가능합니다."
    )
    public ResponseEntity<ResponseDto<Object>> removeTeamMember(
            @PathVariable Integer teamId,
            @PathVariable Integer memberUuid, // studentId 대신 uuid 사용
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Integer adminUuid = userDetails.getId();

        try {
            Object result = adminTeamService.removeTeamMember(teamId, memberUuid, adminUuid);
            return ResponseEntity.ok(ResponseDto.success("팀원이 삭제되었습니다.", result));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ResponseDto.error(e.getMessage()));
        }
    }

    @GetMapping("/{teamId}/attendance")
    @Operation(
            summary = "팀 출석/참여율 통계",
            description = "정규 스터디 팀의 출석 및 참여율 통계를 조회합니다. 번개 스터디에는 적용되지 않습니다."
    )
    public ResponseEntity<ResponseDto<Map<String, Object>>> getTeamAttendanceStats(
            @PathVariable Integer teamId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Integer uuid = userDetails.getId();

        try {
            Map<String, Object> stats = adminTeamService.getTeamAttendanceStats(teamId, uuid);
            return ResponseEntity.ok(ResponseDto.success(stats));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ResponseDto.error(e.getMessage()));
        }
    }

    @GetMapping("/reports/status")
    @Operation(
            summary = "정규 스터디 보고서 제출/평가 현황 조회",
            description = "모든 정규 스터디 팀의 보고서 제출 및 평가 현황을 조회합니다. 번개 스터디는 포함되지 않습니다."
    )
    public ResponseEntity<ResponseDto<Map<String, Object>>> getTeamReportsStatus(
            @RequestParam(required = false) Integer semesterId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Integer uuid = userDetails.getId();

        Map<String, Object> status = adminTeamService.getTeamReportsStatus(semesterId, uuid);
        return ResponseEntity.ok(ResponseDto.success(status));
    }
}