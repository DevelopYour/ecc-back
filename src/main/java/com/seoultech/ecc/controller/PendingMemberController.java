package com.seoultech.ecc.controller;

import com.seoultech.ecc.dto.ResponseDto;
import com.seoultech.ecc.dto.auth.SignupRequest;
import com.seoultech.ecc.dto.member.MemberResponse;
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
@Tag(name = "가입 신청 API", description = "가입 신청 조회, 수정, 취소 등 관련 API")
@SecurityRequirement(name = "Bearer Authentication")
public class PendingMemberController {

    private final MemberService memberService;

    @GetMapping("/signup")
    @Operation(summary = "가입 신청서 조회", description = "내 가입 신청서를 조회합니다.")
    public ResponseEntity<ResponseDto<MemberResponse>> getMyApplication() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String studentId = authentication.getName();
            MemberResponse response = memberService.getPendingApplication(studentId);
            return ResponseEntity.ok(ResponseDto.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error(e.getMessage()));
        }
    }

    @PatchMapping("/signup")
    @Operation(summary = "가입 신청서 수정", description = "내 가입 신청서를 수정합니다.")
    public ResponseEntity<ResponseDto<MemberResponse>> updateMyApplication(@Valid @RequestBody SignupRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String studentId = authentication.getName();
            MemberResponse response = memberService.updatePendingApplication(studentId, request);
            return ResponseEntity.ok(ResponseDto.success("가입 신청서가 성공적으로 수정되었습니다.", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error(e.getMessage()));
        }
    }

    @DeleteMapping("/signup")
    @Operation(summary = "가입 신청 취소", description = "가입 신청을 취소합니다.")
    public ResponseEntity<ResponseDto<Void>> cancelApplication() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String studentId = authentication.getName();
            memberService.cancelPendingApplication(studentId);
            return ResponseEntity.ok(ResponseDto.success("가입 신청이 취소되었습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error(e.getMessage()));
        }
    }
}