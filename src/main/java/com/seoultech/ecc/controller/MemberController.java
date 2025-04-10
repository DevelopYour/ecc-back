package com.seoultech.ecc.controller;

import com.seoultech.ecc.dto.ResponseDto;
import com.seoultech.ecc.dto.member.MemberResponse;
import com.seoultech.ecc.dto.member.PasswordChangeRequest;
import com.seoultech.ecc.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 회원 정보를 조회합니다.")
    public ResponseEntity<ResponseDto<MemberResponse>> getMyInfo() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String studentId = authentication.getName();
            MemberResponse response = memberService.getActiveMemberInfo(studentId);
            return ResponseEntity.ok(ResponseDto.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error(e.getMessage()));
        }
    }

    @PatchMapping("/me/password")
    @Operation(summary = "비밀번호 변경", description = "비밀번호를 변경합니다.")
    public ResponseEntity<ResponseDto<Void>> updatePassword(@RequestBody PasswordChangeRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String studentId = authentication.getName();
            memberService.updatePassword(studentId, request.getCurrentPassword(), request.getNewPassword());
            return ResponseEntity.ok(ResponseDto.success("비밀번호가 변경되었습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error(e.getMessage()));
        }
    }

    @PatchMapping("/me/level")
    @Operation(summary = "영어 실력 레벨 변경 신청", description = "영어 실력 레벨 변경을 신청합니다.")
    public ResponseEntity<ResponseDto<Void>> updateEnglishLevel(@RequestParam("level") Integer level) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String studentId = authentication.getName();
            memberService.requestLevelChange(studentId, level);
            return ResponseEntity.ok(ResponseDto.success("영어 실력 레벨 변경이 요청되었습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error(e.getMessage()));
        }
    }

    @PostMapping("/me/withdraw")
    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 처리합니다.")
    public ResponseEntity<ResponseDto<Void>> withdrawMembership() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String studentId = authentication.getName();
            memberService.withdrawMembership(studentId);
            return ResponseEntity.ok(ResponseDto.success("회원 탈퇴가 완료되었습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error(e.getMessage()));
        }
    }


    // 회원 상태 변경 신청 -> 웹에서 신청하는게 맞나?
    /*@PatchMapping("/me/status")
    @Operation(summary = "회원 상태 변경 신청", description = "회원 상태 변경을 신청합니다.")
    public ResponseEntity<ResponseDto<Void>> updateStatus(@RequestParam("status") MemberStatus status) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String studentId = authentication.getName();
            memberService.updateStatus(studentId, status);
            return ResponseEntity.ok(ResponseDto.success("회원 상태 변경이 요청되었습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error(e.getMessage()));
        }
    }*/
}