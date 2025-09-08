package com.seoultech.ecc.admin.controller;

import com.seoultech.ecc.admin.dto.AssignedTeamDto;
import com.seoultech.ecc.admin.service.AdminTeamMatchService;
import com.seoultech.ecc.member.dto.ResponseDto;
import com.seoultech.ecc.team.dto.ApplyStudyDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/team-match")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "팀 배정 API", description = "관리자 전용 팀 배정 API")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminTeamMatchController {

    private final AdminTeamMatchService service;

    @GetMapping("/applications")
    @Operation(summary = "전체 팀 조회", description = "모든 팀 목록을 조회합니다. 정규 스터디와 번개 스터디를 필터링할 수 있습니다.")
    public ResponseEntity<ResponseDto<List<ApplyStudyDto.ApplyResponse>>> getRegularApplications() {
        List<ApplyStudyDto.ApplyResponse> response = service.getRegularApplicants();
        return ResponseEntity.ok(ResponseDto.success(response));
    }

    @GetMapping
    @Operation(summary = "팀 매칭 실행", description = "OR-Tools 최적화 알고리즘을 실행 후 팀 매칭 결과를 반환합니다.")
    public ResponseEntity<ResponseDto<List<AssignedTeamDto>>> executeTeamAssignment() {
        List<AssignedTeamDto> response = service.assignTeams();
        return ResponseEntity.ok(ResponseDto.success(response));
    }


    @PostMapping
    @Operation(summary = "팀 배정 결과 저장", description = "배정된 팀 결과를 저장합니다.")
    public ResponseEntity<ResponseDto<Integer>> saveTeamAssignment(
            @RequestBody List<AssignedTeamDto> results) {
        Integer numberOfTeams = service.saveTeams(results);
        return ResponseEntity.ok(ResponseDto.success(numberOfTeams));
    }

    // TODO: 신청 내역 전체 삭제

}
