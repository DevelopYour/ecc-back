package com.seoultech.ecc.team.controller;

import com.seoultech.ecc.member.dto.ResponseDto;
import com.seoultech.ecc.member.dto.CustomUserDetails;
import com.seoultech.ecc.team.dto.TeamDto;
import com.seoultech.ecc.team.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
@Tag(name = "팀 API", description = "팀 정보 조회 등 팀 관련 API")
@SecurityRequirement(name = "Bearer Authentication")
public class TeamController {

    private final TeamService teamService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "내가 속한 모든 팀 조회", description = "현재 로그인한 회원이 속한 모든 팀 목록을 조회합니다.")
    public ResponseEntity<ResponseDto<List<TeamDto>>> getMyTeams(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer uuid = userDetails.getId();

        List<TeamDto> teams = teamService.getTeamsByMember(uuid);
        return ResponseEntity.ok(ResponseDto.success(teams));
    }

    @GetMapping("/me/regular")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "내가 속한 정규 스터디 팀 조회", description = "현재 로그인한 회원이 속한 정규 스터디 팀 목록을 조회합니다.")
    public ResponseEntity<ResponseDto<List<TeamDto>>> getMyRegularTeams(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer uuid = userDetails.getId();

        List<TeamDto> teams = teamService.getRegularTeamsByMember(uuid);
        return ResponseEntity.ok(ResponseDto.success(teams));
    }

    @GetMapping("/me/one-time")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "내가 속한 번개 스터디 팀 조회", description = "현재 로그인한 회원이 속한 번개 스터디 팀 목록을 조회합니다.")
    public ResponseEntity<ResponseDto<List<TeamDto>>> getMyOneTimeTeams(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer uuid = userDetails.getId();

        List<TeamDto> teams = teamService.getOneTimeTeamsByMember(uuid);
        return ResponseEntity.ok(ResponseDto.success(teams));
    }

    @GetMapping("/{teamId}")
    @PreAuthorize("@teamMemberChecker.isTeamMember(#teamId, authentication.credentials)")
    @Operation(summary = "팀 상세 정보 조회", description = "특정 팀의 상세 정보를 조회합니다. 해당 팀에 속한 회원만 조회할 수 있습니다.")
    public ResponseEntity<ResponseDto<TeamDto>> getTeamDetail(
            @PathVariable Long teamId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer uuid = userDetails.getId();

        TeamDto teamDetail = teamService.getTeamDetail(teamId, uuid);
        return ResponseEntity.ok(ResponseDto.success(teamDetail));
    }
}