package com.seoultech.ecc.admin.controller;

import com.seoultech.ecc.member.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/admin/team-match")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "팀 배정 API", description = "관리자 전용 팀 배정 API")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminTeamMatchController {

//    @GetMapping
//    @Operation(summary = "전체 팀 조회", description = "모든 팀 목록을 조회합니다. 정규 스터디와 번개 스터디를 필터링할 수 있습니다.")
//    public ResponseEntity<ResponseDto<TeamMatchDto>> getTeamMatch()

}
