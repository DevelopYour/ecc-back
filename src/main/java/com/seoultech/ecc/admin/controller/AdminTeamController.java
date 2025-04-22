package com.seoultech.ecc.admin.controller;

import com.seoultech.ecc.admin.service.AdminTeamService;
import com.seoultech.ecc.member.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/teams")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "팀 관리 API", description = "관리자 전용 팀 관리 API")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminTeamController {

    private final AdminTeamService adminTeamService;

    @DeleteMapping("/one-time/{teamId}")
    @Operation(
            summary = "번개 스터디 삭제",
            description = "번개 스터디를 데이터베이스에서 완전히 삭제합니다. 관리자만 삭제 가능합니다."
    )
    public ResponseEntity<ResponseDto<Void>> deleteOneTimeTeam(@PathVariable Long teamId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String studentId = authentication.getName();

        adminTeamService.deleteOneTimeTeam(teamId, studentId);
        return ResponseEntity.ok(ResponseDto.success("번개 스터디가 삭제되었습니다.", null));
    }
}