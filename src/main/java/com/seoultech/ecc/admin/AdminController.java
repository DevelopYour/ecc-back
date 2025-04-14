package com.seoultech.ecc.admin;

import com.seoultech.ecc.member.dto.ResponseDto;
import com.seoultech.ecc.member.dto.MemberResponse;
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
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "관리자 API", description = "관리자 전용 API")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    @Operation(summary = "전체 회원 조회", description = "모든 회원 목록을 조회합니다.")
    public ResponseEntity<ResponseDto<List<MemberResponse>>> getAllMembers() {
        List<MemberResponse> members = adminService.getAllMembers();
        return ResponseEntity.ok(ResponseDto.success(members));
    }

    @GetMapping("/users/status/{status}")
    @Operation(summary = "상태별 회원 조회", description = "특정 상태의 회원 목록을 조회합니다.")
    public ResponseEntity<ResponseDto<List<MemberResponse>>> getMembersByStatus(
            @PathVariable("status") MemberStatus status
    ) {
        List<MemberResponse> members = adminService.getMembersByStatus(status);
        return ResponseEntity.ok(ResponseDto.success(members));
    }

    @GetMapping("/users/pending")
    @Operation(summary = "승인 대기 회원 조회", description = "승인 대기 중인 회원 목록을 조회합니다.")
    public ResponseEntity<ResponseDto<List<MemberResponse>>> getPendingMembers() {
        List<MemberResponse> members = adminService.getMembersByStatus(MemberStatus.PENDING);
        return ResponseEntity.ok(ResponseDto.success(members));
    }

    @GetMapping("/users/level/{level}")
    @Operation(summary = "레벨별 회원 조회", description = "특정 영어 레벨의 회원 목록을 조회합니다.")
    public ResponseEntity<ResponseDto<List<MemberResponse>>> getMembersByLevel(
            @PathVariable("level") Integer level
    ) {
        List<MemberResponse> members = adminService.getMembersByLevel(level);
        return ResponseEntity.ok(ResponseDto.success(members));
    }

    @GetMapping("/users/filter")
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

    @PatchMapping("/users/{studentId}/approve")
    @Operation(summary = "회원 가입 승인", description = "대기 중인 회원의 가입을 승인합니다.")
    public ResponseEntity<ResponseDto<MemberResponse>> approveApplication(
            @PathVariable String studentId
    ) {
        MemberResponse response = adminService.approveApplication(studentId);
        return ResponseEntity.ok(ResponseDto.success("회원 가입이 승인되었습니다.", response));
    }

    @DeleteMapping("/users/{studentId}/reject")
    @Operation(summary = "회원 가입 거절", description = "대기 중인 회원의 가입을 거절하고 회원 정보를 삭제합니다.")
    public ResponseEntity<ResponseDto<Void>> rejectApplication(
            @PathVariable String studentId
    ) {
        adminService.rejectApplication(studentId);
        return ResponseEntity.ok(ResponseDto.success("회원 가입이 거절되었습니다.", null));
    }

    @PatchMapping("/users/{studentId}/status")
    @Operation(summary = "회원 상태 변경", description = "회원의 상태를 변경합니다.")
    public ResponseEntity<ResponseDto<MemberResponse>> updateMemberStatus(
            @PathVariable String studentId,
            @RequestParam MemberStatus status
    ) {
        MemberResponse response = adminService.updateMemberStatus(studentId, status);
        return ResponseEntity.ok(ResponseDto.success("회원 상태가 변경되었습니다.", response));
    }

    @PatchMapping("/users/{studentId}/ban")
    @Operation(summary = "회원 강제 탈퇴", description = "회원을 강제 탈퇴시킵니다(BANNED 상태로 변경).")
    public ResponseEntity<ResponseDto<MemberResponse>> banMember(
            @PathVariable String studentId
    ) {
        MemberResponse response = adminService.updateMemberStatus(studentId, MemberStatus.BANNED);
        return ResponseEntity.ok(ResponseDto.success("회원이 강제 탈퇴 처리되었습니다.", response));
    }

    @PatchMapping("/users/{studentId}/suspend")
    @Operation(summary = "회원 일시 정지", description = "회원을 일시 정지시킵니다(SUSPENDED 상태로 변경).")
    public ResponseEntity<ResponseDto<MemberResponse>> suspendMember(
            @PathVariable String studentId
    ) {
        MemberResponse response = adminService.updateMemberStatus(studentId, MemberStatus.SUSPENDED);
        return ResponseEntity.ok(ResponseDto.success("회원이 일시 정지 처리되었습니다.", response));
    }

    @PatchMapping("/users/{studentId}/activate")
    @Operation(summary = "회원 활성화", description = "정지된 회원을 다시 활성화합니다(ACTIVE 상태로 변경).")
    public ResponseEntity<ResponseDto<MemberResponse>> activateMember(
            @PathVariable String studentId
    ) {
        MemberResponse response = adminService.updateMemberStatus(studentId, MemberStatus.ACTIVE);
        return ResponseEntity.ok(ResponseDto.success("회원이 활성화 처리되었습니다.", response));
    }

    @PatchMapping("/users/{studentId}/level")
    @Operation(summary = "회원 영어 레벨 변경", description = "회원의 영어 레벨을 변경합니다.")
    public ResponseEntity<ResponseDto<MemberResponse>> updateMemberLevel(
            @PathVariable String studentId,
            @RequestParam Integer level
    ) {
        MemberResponse response = adminService.updateMemberLevel(studentId, level);
        return ResponseEntity.ok(ResponseDto.success("회원의 영어 레벨이 변경되었습니다.", response));
    }
}