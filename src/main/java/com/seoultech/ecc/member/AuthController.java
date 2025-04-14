package com.seoultech.ecc.member;

import com.seoultech.ecc.member.dto.ResponseDto;
import com.seoultech.ecc.member.dto.LoginRequest;
import com.seoultech.ecc.member.dto.SignupRequest;
import com.seoultech.ecc.member.dto.TokenRefreshRequest;
import com.seoultech.ecc.member.dto.TokenResponse;
import com.seoultech.ecc.member.dto.MemberResponse;
import com.seoultech.ecc.service.AuthService;
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
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "인증 API", description = "회원가입, 로그인, 로그아웃 등 인증 관련 API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "가입 신청 상태로 회원가입합니다.")
    public ResponseEntity<ResponseDto<MemberResponse>> signup(@Valid @RequestBody SignupRequest request) {
        MemberResponse response = authService.signup(request);
        return ResponseEntity.ok(ResponseDto.success("회원가입이 완료되었습니다. 관리자 승인 후 로그인이 가능합니다.", response));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "학번과 비밀번호로 로그인하고 JWT 토큰을 발급받습니다.")
    public ResponseEntity<ResponseDto<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse response = authService.login(request);
        return ResponseEntity.ok(ResponseDto.success("로그인이 성공적으로 완료되었습니다.", response));
    }

    @PostMapping("/refresh")
    @Operation(summary = "토큰 갱신", description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다.")
    public ResponseEntity<ResponseDto<TokenResponse>> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        TokenResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ResponseDto.success("토큰이 갱신되었습니다.", response));
    }

    @PostMapping("/logout")
    @Operation(
            summary = "로그아웃",
            description = "로그아웃 처리합니다. 리프레시 토큰을 무효화하고 서버의 인증 정보를 삭제합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<ResponseDto<Void>> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String studentId = authentication.getName();

        authService.logout(studentId);
        return ResponseEntity.ok(ResponseDto.success("로그아웃이 완료되었습니다.", null));
    }
}