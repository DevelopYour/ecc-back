package com.seoultech.ecc.controller;

import com.seoultech.ecc.domain.Member;
import com.seoultech.ecc.dto.MemberDto;
import com.seoultech.ecc.mapper.MemberMapper;
import com.seoultech.ecc.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberMapper mapper;

    @PostMapping("/auth/signup")
    @Operation(summary = "회원가입", description = "")
    public ResponseEntity<MemberDto> signup(@RequestBody MemberDto dto) {
        Member member = memberService.signup(mapper.toModel(dto));
        return ResponseEntity.ok(mapper.toDto(member));
    }

    @PostMapping("/auth/login")
    @Operation(summary = "로그인", description = "")
    public ResponseEntity<LoginResponse> login(@RequestBody MemberDto dto) {
        return ResponseEntity.ok(memberService.login(dto));
    }

    @PostMapping("/auth/logout")
    @Operation(summary = "로그아웃", description = "")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        memberService.logout(token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/me")
    @Operation(summary = "내 정보 조회", description = "")
    public ResponseEntity<MemberDto> getMyInfo() {
        return ResponseEntity.ok(memberService.getMyInfo());
    }

    @PatchMapping("/users/me")
    @Operation(summary = "내 정보 수정", description = "")
    public ResponseEntity<Void> updateMyInfo(@RequestBody MemberDto dto) {
        memberService.updateMyInfo(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/me")
    @Operation(summary = "회원 탈퇴 요청", description = "")
    public ResponseEntity<Void> deleteMyAccount() {
        memberService.deleteMyAccount();
        return ResponseEntity.ok().build();
    }

    // 영어 실력 레벨 변경 신청
    @PatchMapping("/users/me/level")
    public ResponseEntity<Void> updateEnglishLevel(@RequestBody MemberDto dto) {
        memberService.updateEnglishLevel(request);
        return ResponseEntity.ok().build();
    }

    // 회원 상태 변경
    @PatchMapping("/users/me/status")
    public ResponseEntity<Void> updateStatus(@RequestBody MemberDto dto) {
        memberService.updateStatus(request);
        return ResponseEntity.ok().build();
    }
}
