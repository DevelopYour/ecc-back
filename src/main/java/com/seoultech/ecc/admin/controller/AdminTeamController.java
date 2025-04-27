package com.seoultech.ecc.admin.controller;

import com.seoultech.ecc.admin.service.AdminTeamService;
import com.seoultech.ecc.member.dto.ResponseDto;
import com.seoultech.ecc.report.datamodel.ReportDocument;
import com.seoultech.ecc.team.dto.TeamDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/teams")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "팀 관리 API", description = "관리자 전용 팀 관리 API")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminTeamController {

    private final AdminTeamService adminTeamService;

    @GetMapping
    @Operation(summary = "전체 팀 조회", description = "모든 팀 목록을 조회합니다.")
    public ResponseEntity<ResponseDto<List<TeamDto>>> getAllTeams(
            @RequestParam(required = false) Boolean isRegular,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer semester) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminId = authentication.getName();

        List<TeamDto> teams = adminTeamService.getAllTeams(adminId, isRegular, year, semester);
        return ResponseEntity.ok(ResponseDto.success(teams));
    }

    @GetMapping("/{teamId}")
    @Operation(summary = "팀 상세 정보 조회", description = "특정 팀의 상세 정보를 조회합니다.")
    public ResponseEntity<ResponseDto<TeamDto>> getTeamDetail(@PathVariable Long teamId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminId = authentication.getName();

        TeamDto teamDetail = adminTeamService.getTeamDetail(teamId, adminId);
        return ResponseEntity.ok(ResponseDto.success(teamDetail));
    }

    @GetMapping("/{teamId}/{week}")
    @Operation(summary = "팀 주차별 상세 정보 조회", description = "특정 팀의 주차별 상세 정보를 조회합니다.")
    public ResponseEntity<ResponseDto<Object>> getTeamWeekDetail(
            @PathVariable Long teamId,
            @PathVariable int week) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminId = authentication.getName();

        Object weekDetail = adminTeamService.getTeamWeekDetail(teamId, week, adminId);
        return ResponseEntity.ok(ResponseDto.success(weekDetail));
    }

    @GetMapping("/{teamId}/{week}/report")
    @Operation(summary = "팀 주차별 보고서 조회", description = "특정 팀의 주차별 보고서를 조회합니다.")
    public ResponseEntity<ResponseDto<ReportDocument>> getTeamWeekReport(
            @PathVariable Long teamId,
            @PathVariable int week) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminId = authentication.getName();

        ReportDocument report = adminTeamService.getTeamWeekReport(teamId, week, adminId);
        return ResponseEntity.ok(ResponseDto.success(report));
    }

    @PatchMapping("/{teamId}/{week}/report/grade")
    @Operation(summary = "팀 보고서 평가 점수 수정", description = "특정 팀의 보고서 평가 점수를 수정합니다.")
    public ResponseEntity<ResponseDto<ReportDocument>> updateReportGrade(
            @PathVariable Long teamId,
            @PathVariable int week,
            @RequestParam int grade) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminId = authentication.getName();

        ReportDocument updatedReport = adminTeamService.updateReportGrade(teamId, week, grade, adminId);
        return ResponseEntity.ok(ResponseDto.success("보고서 평가 점수가 수정되었습니다.", updatedReport));
    }

    @DeleteMapping("/one-time/{teamId}")
    @Operation(
            summary = "번개 스터디 삭제",
            description = "번개 스터디를 데이터베이스에서 완전히 삭제합니다. 관리자만 삭제 가능합니다."
    )
    public ResponseEntity<ResponseDto<Void>> deleteOneTimeTeam(@PathVariable Long teamId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminId = authentication.getName();

        adminTeamService.deleteOneTimeTeam(teamId, adminId);
        return ResponseEntity.ok(ResponseDto.success("번개 스터디가 삭제되었습니다.", null));
    }

    @PatchMapping("/{teamId}/score")
    @Operation(summary = "팀 점수 수동 조정", description = "특정 팀의 점수를 수동으로 조정합니다.")
    public ResponseEntity<ResponseDto<TeamDto>> updateTeamScore(
            @PathVariable Long teamId,
            @RequestParam int score) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminId = authentication.getName();

        TeamDto updatedTeam = adminTeamService.updateTeamScore(teamId, score, adminId);
        return ResponseEntity.ok(ResponseDto.success("팀 점수가 수정되었습니다.", updatedTeam));
    }

    @GetMapping("/{teamId}/members")
    @Operation(summary = "팀 멤버 조회", description = "특정 팀의 멤버 목록을 조회합니다.")
    public ResponseEntity<ResponseDto<Object>> getTeamMembers(@PathVariable Long teamId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminId = authentication.getName();

        Object members = adminTeamService.getTeamMembers(teamId, adminId);
        return ResponseEntity.ok(ResponseDto.success(members));
    }

    @PostMapping("/{teamId}/members")
    @Operation(summary = "팀 멤버 추가", description = "특정 팀에 멤버를 추가합니다.")
    public ResponseEntity<ResponseDto<Object>> addTeamMember(
            @PathVariable Long teamId,
            @RequestParam String studentId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminId = authentication.getName();

        Object result = adminTeamService.addTeamMember(teamId, studentId, adminId);
        return ResponseEntity.ok(ResponseDto.success("팀원이 추가되었습니다.", result));
    }

    @DeleteMapping("/{teamId}/members/{studentId}")
    @Operation(summary = "팀 멤버 삭제", description = "특정 팀에서 멤버를 삭제합니다.")
    public ResponseEntity<ResponseDto<Object>> removeTeamMember(
            @PathVariable Long teamId,
            @PathVariable String studentId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminId = authentication.getName();

        Object result = adminTeamService.removeTeamMember(teamId, studentId, adminId);
        return ResponseEntity.ok(ResponseDto.success("팀원이 삭제되었습니다.", result));
    }

    @GetMapping("/{teamId}/attendance")
    @Operation(summary = "팀 출석/참여율 통계", description = "특정 팀의 출석 및 참여율 통계를 조회합니다.")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getTeamAttendanceStats(@PathVariable Long teamId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminId = authentication.getName();

        Map<String, Object> stats = adminTeamService.getTeamAttendanceStats(teamId, adminId);
        return ResponseEntity.ok(ResponseDto.success(stats));
    }

    @GetMapping("/reports/status")
    @Operation(summary = "팀 보고서 제출/평가 현황 조회", description = "모든 팀의 보고서 제출 및 평가 현황을 조회합니다.")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getTeamReportsStatus(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer semester) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminId = authentication.getName();

        Map<String, Object> status = adminTeamService.getTeamReportsStatus(year, semester, adminId);
        return ResponseEntity.ok(ResponseDto.success(status));
    }
}