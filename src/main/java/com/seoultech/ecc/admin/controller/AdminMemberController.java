package com.seoultech.ecc.admin.controller;

import com.seoultech.ecc.admin.service.AdminMemberService;
import com.seoultech.ecc.member.dto.ResponseDto;
import com.seoultech.ecc.member.dto.MemberResponse;
import com.seoultech.ecc.member.dto.level.LevelChangeRequestDto;
import com.seoultech.ecc.member.datamodel.MemberStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "회원 관리 API", description = "관리자 전용 회원 관리 API")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminMemberController {

    private final AdminMemberService adminService;

    @GetMapping("")
    @Operation(summary = "전체 회원 조회", description = "모든 회원 목록을 조회합니다.")
    public ResponseEntity<ResponseDto<List<MemberResponse>>> getAllMembers() {
        List<MemberResponse> members = adminService.getAllMembers();
        return ResponseEntity.ok(ResponseDto.success(members));
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "회원 상세 정보 조회", description = "특정 회원의 상세 정보를 조회합니다. (UUID 사용)")
    public ResponseEntity<ResponseDto<MemberResponse>> getMemberDetail(@PathVariable Integer uuid) {
        MemberResponse member = adminService.getMemberDetail(uuid);
        return ResponseEntity.ok(ResponseDto.success(member));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "상태별 회원 조회", description = "특정 상태의 회원 목록을 조회합니다.")
    public ResponseEntity<ResponseDto<List<MemberResponse>>> getMembersByStatus(
            @PathVariable("status") MemberStatus status
    ) {
        List<MemberResponse> members = adminService.getMembersByStatus(status);
        return ResponseEntity.ok(ResponseDto.success(members));
    }

    @GetMapping("/pending")
    @Operation(summary = "승인 대기 회원 조회", description = "승인 대기 중인 회원 목록을 조회합니다.")
    public ResponseEntity<ResponseDto<List<MemberResponse>>> getPendingMembers() {
        List<MemberResponse> members = adminService.getMembersByStatus(MemberStatus.PENDING);
        return ResponseEntity.ok(ResponseDto.success(members));
    }

    @GetMapping("/level")
    @Operation(summary = "레벨 변경 요청 목록 조회", description = "레벨 변경을 요청한 회원 목록을 조회합니다.")
    public ResponseEntity<ResponseDto<List<LevelChangeRequestDto>>> getLevelChangeRequests() {
        List<LevelChangeRequestDto> requests = adminService.getPendingLevelChangeRequests();
        return ResponseEntity.ok(ResponseDto.success(requests));
    }

    @GetMapping("/level/{level}")
    @Operation(summary = "레벨별 회원 조회", description = "특정 영어 레벨의 회원 목록을 조회합니다.")
    public ResponseEntity<ResponseDto<List<MemberResponse>>> getMembersByLevel(
            @PathVariable("level") Integer level
    ) {
        List<MemberResponse> members = adminService.getMembersByLevel(level);
        return ResponseEntity.ok(ResponseDto.success(members));
    }

    @GetMapping("/filter")
    @Operation(summary = "회원 필터링 조회", description = "상태와 레벨로 회원을 필터링하여 조회합니다.")
    public ResponseEntity<ResponseDto<List<MemberResponse>>> getMembersByFilter(
            @RequestParam(required = false) MemberStatus status,
            @RequestParam(required = false) Integer level
    ) {
        List<MemberResponse> members;

        if (status != null && level != null) {
            // 상태와 레벨 모두로 필터링
            members = adminService.getMembersByStatusAndLevel(status, level);
        } else if (status != null) {
            // 상태로만 필터링
            members = adminService.getMembersByStatus(status);
        } else if (level != null) {
            // 레벨로만 필터링
            members = adminService.getMembersByLevel(level);
        } else {
            // 필터 없이 전체 조회
            members = adminService.getAllMembers();
        }

        return ResponseEntity.ok(ResponseDto.success(members));
    }

    @PatchMapping("/{uuid}/approve")
    @Operation(summary = "회원 가입 승인 (UUID)", description = "대기 중인 회원의 가입을 승인합니다.")
    public ResponseEntity<ResponseDto<MemberResponse>> approveApplication(
            @PathVariable Integer uuid
    ) {
        MemberResponse response = adminService.approveApplication(uuid);
        return ResponseEntity.ok(ResponseDto.success("회원 가입이 승인되었습니다.", response));
    }

    @DeleteMapping("/{uuid}/reject")
    @Operation(summary = "회원 가입 거절 (UUID)", description = "대기 중인 회원의 가입을 거절하고 회원 정보를 삭제합니다.")
    public ResponseEntity<ResponseDto<Void>> rejectApplication(
            @PathVariable Integer uuid
    ) {
        adminService.rejectApplication(uuid);
        return ResponseEntity.ok(ResponseDto.success("회원 가입이 거절되었습니다.", null));
    }

    @PatchMapping("/{uuid}/status")
    @Operation(summary = "회원 상태 변경 (UUID)", description = "회원의 상태를 변경합니다.")
    public ResponseEntity<ResponseDto<MemberResponse>> updateMemberStatus(
            @PathVariable Integer uuid,
            @RequestParam MemberStatus status
    ) {
        MemberResponse response = adminService.updateMemberStatus(uuid, status);
        return ResponseEntity.ok(ResponseDto.success("회원 상태가 변경되었습니다.", response));
    }

    @PatchMapping("/{uuid}/level")
    @Operation(summary = "회원 영어 레벨 변경 (UUID)", description = "회원의 영어 레벨을 변경합니다.")
    public ResponseEntity<ResponseDto<MemberResponse>> updateMemberLevel(
            @PathVariable Integer uuid,
            @RequestParam Integer level
    ) {
        MemberResponse response = adminService.updateMemberLevel(uuid, level);
        return ResponseEntity.ok(ResponseDto.success("회원의 영어 레벨이 변경되었습니다.", response));
    }

    @PatchMapping("/level/{requestId}/approve")
    @Operation(summary = "레벨 변경 요청 승인", description = "회원의 레벨 변경 요청을 승인합니다.")
    public ResponseEntity<ResponseDto<MemberResponse>> approveLevelChangeRequest(
            @PathVariable Integer requestId
    ) {
        MemberResponse response = adminService.approveLevelChangeRequest(requestId);
        return ResponseEntity.ok(ResponseDto.success("레벨 변경 요청이 승인되었습니다.", response));
    }

    @PatchMapping("/level/{requestId}/reject")
    @Operation(summary = "레벨 변경 요청 거절", description = "회원의 레벨 변경 요청을 거절합니다.")
    public ResponseEntity<ResponseDto<Void>> rejectLevelChangeRequest(
            @PathVariable Integer requestId
    ) {
        adminService.rejectLevelChangeRequest(requestId);
        return ResponseEntity.ok(ResponseDto.success("레벨 변경 요청이 거절되었습니다.", null));
    }
}