package com.seoultech.ecc.team.controller;

import com.seoultech.ecc.member.dto.ResponseDto;
import com.seoultech.ecc.member.dto.CustomUserDetails;
import com.seoultech.ecc.team.datamodel.OneTimeTeamStatus;
import com.seoultech.ecc.team.dto.OneTimeTeamDto;
import com.seoultech.ecc.team.service.OneTimeTeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teams/one-time")
@RequiredArgsConstructor
@Tag(name = "번개 스터디 API", description = "번개 스터디 관련 API")
@SecurityRequirement(name = "Bearer Authentication")
public class OneTimeTeamController {

    private final OneTimeTeamService oneTimeTeamService;

    @GetMapping
    @Operation(summary = "전체 번개 스터디 목록 조회", description = "모든 번개 스터디 목록을 조회합니다.")
    public ResponseEntity<ResponseDto<OneTimeTeamDto.ListResponse>> getOneTimeTeams() {
        OneTimeTeamDto.ListResponse response = oneTimeTeamService.getAllOneTimeTeams();
        return ResponseEntity.ok(ResponseDto.success(response));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "상태별 번개 스터디 목록 조회", description = "특정 상태의 번개 스터디 목록을 조회합니다.")
    public ResponseEntity<ResponseDto<OneTimeTeamDto.ListResponse>> getOneTimeTeamsByStatus(
            @PathVariable OneTimeTeamStatus status
    ) {
        OneTimeTeamDto.ListResponse response = oneTimeTeamService.getOneTimeTeamsByStatus(status);
        return ResponseEntity.ok(ResponseDto.success(response));
    }

    @GetMapping("/{teamId}")
    @Operation(summary = "번개 스터디 상세 정보 조회", description = "특정 번개 스터디의 상세 정보를 조회합니다.")
    public ResponseEntity<ResponseDto<OneTimeTeamDto.DetailResponse>> getOneTimeTeamDetail(
            @PathVariable Long teamId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer uuid = userDetails.getId();
        String studentId = userDetails.getUsername(); // 이름 표시 등의 목적으로 사용

        OneTimeTeamDto.DetailResponse response = oneTimeTeamService.getOneTimeTeamDetail(teamId, uuid);
        return ResponseEntity.ok(ResponseDto.success(response));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "번개 스터디 생성", description = "새로운 번개 스터디를 생성합니다.")
    public ResponseEntity<ResponseDto<OneTimeTeamDto.Response>> createOneTimeTeam(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody OneTimeTeamDto.CreateRequest request
    ) {
        Integer uuid = userDetails.getId();

        OneTimeTeamDto.Response response = oneTimeTeamService.createOneTimeTeam(uuid, request);
        return ResponseEntity.ok(ResponseDto.success("번개 스터디가 생성되었습니다.", response));
    }

    @PutMapping("/{teamId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "번개 스터디 수정",
            description = "번개 스터디 정보를 수정합니다. 생성자 또는 관리자만 수정 가능합니다. " +
                    "이미 시작된 스터디는 종료 시간, 위치, 설명만 수정 가능합니다."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "번개 스터디 수정 정보",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = OneTimeTeamDto.UpdateRequest.class)
            )
    )
    public ResponseEntity<ResponseDto<OneTimeTeamDto.Response>> updateOneTimeTeam(
            @PathVariable Long teamId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody OneTimeTeamDto.UpdateRequest request
    ) {
        Integer uuid = userDetails.getId();

        OneTimeTeamDto.Response response = oneTimeTeamService.updateOneTimeTeam(teamId, uuid, request);
        return ResponseEntity.ok(ResponseDto.success("번개 스터디 정보가 수정되었습니다.", response));
    }

    @PatchMapping("/{teamId}/apply")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "번개 스터디 신청", description = "번개 스터디에 참여 신청합니다.")
    public ResponseEntity<ResponseDto<OneTimeTeamDto.Response>> applyToOneTimeTeam(
            @PathVariable Long teamId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer uuid = userDetails.getId();

        OneTimeTeamDto.Response response = oneTimeTeamService.applyToOneTimeTeam(teamId, uuid);
        return ResponseEntity.ok(ResponseDto.success("번개 스터디 신청이 완료되었습니다.", response));
    }

    @DeleteMapping("/{teamId}/apply")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "번개 스터디 신청 취소", description = "번개 스터디 참여 신청을 취소합니다. 스터디 시작 3시간 전까지만 취소 가능합니다.")
    public ResponseEntity<ResponseDto<OneTimeTeamDto.Response>> cancelOneTimeTeamApplication(
            @PathVariable Long teamId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer uuid = userDetails.getId();

        OneTimeTeamDto.Response response = oneTimeTeamService.cancelOneTimeTeamApplication(teamId, uuid);
        return ResponseEntity.ok(ResponseDto.success("번개 스터디 신청이 취소되었습니다.", response));
    }

    @DeleteMapping("/{teamId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "번개 스터디 취소", description = "번개 스터디를 취소합니다. 생성자만 취소 가능합니다.")
    public ResponseEntity<ResponseDto<Void>> cancelOneTimeTeam(
            @PathVariable Long teamId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer uuid = userDetails.getId();

        oneTimeTeamService.cancelOneTimeTeam(teamId, uuid);
        return ResponseEntity.ok(ResponseDto.success("번개 스터디가 취소되었습니다.", null));
    }
}