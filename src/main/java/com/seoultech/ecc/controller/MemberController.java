package com.seoultech.ecc.controller;

import com.seoultech.ecc.dto.ResponseDto;
import com.seoultech.ecc.dto.auth.SignupRequest;
import com.seoultech.ecc.dto.member.MemberResponse;
import com.seoultech.ecc.entity.MemberStatus;
import com.seoultech.ecc.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 회원 정보를 조회합니다.")
    public ResponseEntity<ResponseDto<MemberResponse>> getMyInfo(HttpSession session) {
        try {
            MemberResponse response = memberService.getMyInfo(session);
            return ResponseEntity.ok(ResponseDto.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error(e.getMessage()));
        }
    }

    @PatchMapping("/me")
    @Operation(summary = "내 정보 수정", description = "가입 신청 정보를 수정합니다. (PENDING 상태일 때만 가능)")
    public ResponseEntity<ResponseDto<MemberResponse>> updateMyInfo(
            @Valid @RequestBody SignupRequest request,
            HttpSession session) {
        try {
            MemberResponse response = memberService.updateMyInfo(session, request);
            return ResponseEntity.ok(ResponseDto.success("지원서가 성공적으로 수정되었습니다.", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error(e.getMessage()));
        }
    }

    @DeleteMapping("/me")
    @Operation(summary = "가입 신청 취소", description = "가입 신청을 취소합니다. (PENDING 상태일 때만 가능)")
    public ResponseEntity<ResponseDto<Void>> cancelApplication(HttpSession session) {
        try {
            memberService.deleteMyAccount(session);
            return ResponseEntity.ok(ResponseDto.success("지원이 성공적으로 취소되었습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error(e.getMessage()));
        }
    }

    @PatchMapping("/me/level")
    @Operation(summary = "영어 실력 레벨 변경 신청", description = "영어 실력 레벨 변경을 신청합니다.")
    public ResponseEntity<ResponseDto<Void>> updateEnglishLevel(
            @RequestParam("level") Integer level,
            HttpSession session) {
        try {
            memberService.updateEnglishLevel(session, level);
            return ResponseEntity.ok(ResponseDto.success("영어 실력 레벨 변경이 요청되었습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error(e.getMessage()));
        }
    }

    @PatchMapping("/me/status")
    @Operation(summary = "회원 상태 변경 신청", description = "회원 상태 변경을 신청합니다.")
    public ResponseEntity<ResponseDto<Void>> updateStatus(
            @RequestParam("status") MemberStatus status,
            HttpSession session) {
        try {
            memberService.updateStatus(session, status);
            return ResponseEntity.ok(ResponseDto.success("회원 상태 변경이 요청되었습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error(e.getMessage()));
        }
    }
}