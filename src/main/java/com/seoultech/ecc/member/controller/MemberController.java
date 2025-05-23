package com.seoultech.ecc.member.controller;

import com.seoultech.ecc.member.service.MemberService;
import com.seoultech.ecc.member.dto.ResponseDto;
import com.seoultech.ecc.member.dto.SignupRequest;
import com.seoultech.ecc.member.dto.MemberResponse;
import com.seoultech.ecc.member.dto.PasswordChangeRequest;
import com.seoultech.ecc.member.dto.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "회원 API", description = "회원 정보 조회, 수정 등 회원 관련 API")
@SecurityRequirement(name = "Bearer Authentication")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 회원 정보를 조회합니다. 상태에 따라 적절한 정보를 반환합니다.")
    public ResponseEntity<ResponseDto<MemberResponse>> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer uuid = userDetails.getId();

        MemberResponse response = memberService.getMemberInfo(uuid);
        return ResponseEntity.ok(ResponseDto.success(response));
    }

    @PatchMapping("/me/update")
    @Operation(summary = "회원 정보 수정", description = "현재 로그인한 회원의 정보를 수정합니다. PENDING 상태인 경우 가입 신청서를 수정합니다.")
    public ResponseEntity<ResponseDto<MemberResponse>> updateMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody SignupRequest request) {
        Integer uuid = userDetails.getId();

        MemberResponse response = memberService.updatePendingApplication(uuid, request);
        return ResponseEntity.ok(ResponseDto.success("정보가 성공적으로 수정되었습니다.", response));
    }

    @DeleteMapping("/me/cancel")
    @Operation(summary = "가입 신청 취소", description = "가입 신청을 취소합니다. PENDING 상태인 경우에만 가능합니다.")
    public ResponseEntity<ResponseDto<Void>> cancelApplication(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer uuid = userDetails.getId();

        memberService.cancelPendingApplication(uuid);
        return ResponseEntity.ok(ResponseDto.success("가입 신청이 취소되었습니다.", null));
    }

    @PatchMapping("/me/password")
    @Operation(summary = "비밀번호 변경", description = "비밀번호를 변경합니다. ACTIVE 상태인 경우에만 가능합니다.")
    public ResponseEntity<ResponseDto<Void>> updatePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PasswordChangeRequest request) {
        Integer uuid = userDetails.getId();

        memberService.updatePassword(uuid, request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.ok(ResponseDto.success("비밀번호가 변경되었습니다.", null));
    }

    @PatchMapping("/me/level")
    @Operation(summary = "영어 실력 레벨 변경 신청", description = "영어 실력 레벨 변경을 신청합니다. ACTIVE 상태인 경우에만 가능합니다.")
    public ResponseEntity<ResponseDto<Void>> updateEnglishLevel(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("level") Integer level) {
        Integer uuid = userDetails.getId();

        memberService.requestLevelChange(uuid, level);
        return ResponseEntity.ok(ResponseDto.success("영어 실력 레벨 변경이 요청되었습니다.", null));
    }

    @PostMapping("/me/withdraw")
    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 처리합니다. ACTIVE 상태인 경우에만 가능합니다.")
    public ResponseEntity<ResponseDto<Void>> withdrawMembership(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer uuid = userDetails.getId();

        memberService.withdrawMembership(uuid);
        return ResponseEntity.ok(ResponseDto.success("회원 탈퇴가 완료되었습니다.", null));
    }
}