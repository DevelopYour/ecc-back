package com.seoultech.ecc.controller;

import com.seoultech.ecc.dto.ResponseDto;
import com.seoultech.ecc.dto.auth.SignupRequest;
import com.seoultech.ecc.dto.member.MemberResponse;
import com.seoultech.ecc.dto.member.PasswordChangeRequest;
import com.seoultech.ecc.entity.MemberStatus;
import com.seoultech.ecc.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "회원 API", description = "회원 정보 조회, 수정 등 회원 관련 API")
@SecurityRequirement(name = "Bearer Authentication")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 회원 정보를 조회합니다. 상태에 따라 적절한 정보를 반환합니다.")
    public ResponseEntity<ResponseDto<MemberResponse>> getMyInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String studentId = authentication.getName();

        MemberResponse response = memberService.getMemberInfo(studentId);
        return ResponseEntity.ok(ResponseDto.success(response));
    }

    @PatchMapping("/me/update")
    @Operation(summary = "회원 정보 수정", description = "현재 로그인한 회원의 정보를 수정합니다. PENDING 상태인 경우 가입 신청서를 수정합니다.")
    public ResponseEntity<ResponseDto<MemberResponse>> updateMyInfo(@Valid @RequestBody SignupRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String studentId = authentication.getName();

        MemberResponse response = memberService.updatePendingApplication(studentId, request);
        return ResponseEntity.ok(ResponseDto.success("정보가 성공적으로 수정되었습니다.", response));
    }

    @DeleteMapping("/me/cancel")
    @Operation(summary = "가입 신청 취소", description = "가입 신청을 취소합니다. PENDING 상태인 경우에만 가능합니다.")
    public ResponseEntity<ResponseDto<Void>> cancelApplication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String studentId = authentication.getName();

        memberService.cancelPendingApplication(studentId);
        return ResponseEntity.ok(ResponseDto.success("가입 신청이 취소되었습니다.", null));
    }

    @PatchMapping("/me/password")
    @Operation(summary = "비밀번호 변경", description = "비밀번호를 변경합니다. ACTIVE 상태인 경우에만 가능합니다.")
    public ResponseEntity<ResponseDto<Void>> updatePassword(@Valid @RequestBody PasswordChangeRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String studentId = authentication.getName();

        memberService.updatePassword(studentId, request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.ok(ResponseDto.success("비밀번호가 변경되었습니다.", null));
    }

    @PatchMapping("/me/level")
    @Operation(summary = "영어 실력 레벨 변경 신청", description = "영어 실력 레벨 변경을 신청합니다. ACTIVE 상태인 경우에만 가능합니다.")
    public ResponseEntity<ResponseDto<Void>> updateEnglishLevel(@RequestParam("level") Integer level) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String studentId = authentication.getName();

        memberService.requestLevelChange(studentId, level);
        return ResponseEntity.ok(ResponseDto.success("영어 실력 레벨 변경이 요청되었습니다.", null));
    }

    @PostMapping("/me/withdraw")
    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 처리합니다. ACTIVE 상태인 경우에만 가능합니다.")
    public ResponseEntity<ResponseDto<Void>> withdrawMembership() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String studentId = authentication.getName();

        memberService.withdrawMembership(studentId);
        return ResponseEntity.ok(ResponseDto.success("회원 탈퇴가 완료되었습니다.", null));
    }
}