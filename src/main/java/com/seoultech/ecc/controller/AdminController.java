package com.seoultech.ecc.controller;

import com.seoultech.ecc.dto.ResponseDto;
import com.seoultech.ecc.dto.member.MemberResponse;
import com.seoultech.ecc.entity.MemberStatus;
import com.seoultech.ecc.service.MemberService;
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

    private final MemberService memberService;

    @GetMapping("/users")
    @Operation(summary = "전체 회원 조회", description = "모든 회원 목록을 조회합니다.")
    public ResponseEntity<ResponseDto<List<MemberResponse>>> getAllMembers() {
        try {
            List<MemberResponse> members = memberService.getAllMembers();
            return ResponseEntity.ok(ResponseDto.success(members));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error(e.getMessage()));
        }
    }

    @GetMapping("/users/pending")
    @Operation(summary = "승인 대기 회원 조회", description = "승인 대기 중인 회원 목록을 조회합니다.")
    public ResponseEntity<ResponseDto<List<MemberResponse>>> getPendingMembers() {
        try {
            List<MemberResponse> members = memberService.getMembersByStatus(MemberStatus.PENDING);
            return ResponseEntity.ok(ResponseDto.success(members));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error(e.getMessage()));
        }
    }

    @PatchMapping("/users/{studentId}/approve")
    @Operation(summary = "회원 가입 승인", description = "대기 중인 회원의 가입을 승인합니다.")
    public ResponseEntity<ResponseDto<MemberResponse>> approveApplication(@PathVariable String studentId) {
        try {
            MemberResponse response = memberService.approveApplication(studentId);
            return ResponseEntity.ok(ResponseDto.success("회원 가입이 승인되었습니다.", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error(e.getMessage()));
        }
    }

    @DeleteMapping("/users/{studentId}/reject")
    @Operation(summary = "회원 가입 거절", description = "대기 중인 회원의 가입을 거절하고 회원 정보를 삭제합니다.")
    public ResponseEntity<ResponseDto<Void>> rejectApplication(@PathVariable String studentId) {
        try {
            memberService.rejectApplication(studentId);
            return ResponseEntity.ok(ResponseDto.success("회원 가입이 거절되었습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error(e.getMessage()));
        }
    }

    @PatchMapping("/users/{studentId}/ban")
    @Operation(summary = "회원 강제 탈퇴", description = "회원을 강제 탈퇴시킵니다(BANNED 상태로 변경).")
    public ResponseEntity<ResponseDto<MemberResponse>> banMember(@PathVariable String studentId) {
        try {
            MemberResponse response = memberService.updateMemberStatus(studentId, MemberStatus.BANNED);
            return ResponseEntity.ok(ResponseDto.success("회원이 강제 탈퇴 처리되었습니다.", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error(e.getMessage()));
        }
    }

    @PatchMapping("/users/{studentId}/suspend")
    @Operation(summary = "회원 일시 정지", description = "회원을 일시 정지시킵니다(SUSPENDED 상태로 변경).")
    public ResponseEntity<ResponseDto<MemberResponse>> suspendMember(@PathVariable String studentId) {
        try {
            MemberResponse response = memberService.updateMemberStatus(studentId, MemberStatus.SUSPENDED);
            return ResponseEntity.ok(ResponseDto.success("회원이 일시 정지 처리되었습니다.", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error(e.getMessage()));
        }
    }

    @PatchMapping("/users/{studentId}/activate")
    @Operation(summary = "회원 활성화", description = "정지된 회원을 다시 활성화합니다(ACTIVE 상태로 변경).")
    public ResponseEntity<ResponseDto<MemberResponse>> activateMember(@PathVariable String studentId) {
        try {
            MemberResponse response = memberService.updateMemberStatus(studentId, MemberStatus.ACTIVE);
            return ResponseEntity.ok(ResponseDto.success("회원이 활성화 처리되었습니다.", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error(e.getMessage()));
        }
    }
}