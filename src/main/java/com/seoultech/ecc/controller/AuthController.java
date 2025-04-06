package com.seoultech.ecc.controller;

import com.seoultech.ecc.dto.ResponseDto;
import com.seoultech.ecc.dto.auth.LoginRequest;
import com.seoultech.ecc.dto.auth.LoginResponse;
import com.seoultech.ecc.dto.auth.SignupRequest;
import com.seoultech.ecc.dto.member.MemberResponse;
import com.seoultech.ecc.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "가입 신청 상태로 회원가입합니다.")
    public ResponseEntity<ResponseDto<MemberResponse>> signup(@Valid @RequestBody SignupRequest request) {
        try {
            MemberResponse response = authService.signup(request);
            return ResponseEntity.ok(ResponseDto.success("회원가입이 완료되었습니다. 관리자 승인 후 로그인이 가능합니다.", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "학번과 비밀번호로 로그인합니다.")
    public ResponseEntity<ResponseDto<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpSession session) {
        try {
            LoginResponse response = authService.login(request, session);
            return ResponseEntity.ok(ResponseDto.success("로그인이 성공적으로 완료되었습니다.", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error(e.getMessage()));
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그아웃 처리합니다.")
    public ResponseEntity<ResponseDto<Void>> logout(
            @RequestHeader(value = "Authorization", required = false) String token,
            HttpSession session) {
        try {
            authService.logout(token, session);
            return ResponseEntity.ok(ResponseDto.success("로그아웃이 완료되었습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error(e.getMessage()));
        }
    }
}