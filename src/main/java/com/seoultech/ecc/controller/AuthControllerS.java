package com.seoultech.ecc.controller;

import com.seoultech.ecc.dto.MemberCreateDto;
import com.seoultech.ecc.dto.ResponseDto;
import com.seoultech.ecc.mapper.MemberMapper;
import com.seoultech.ecc.service.AuthServiceS;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth-s")
@RequiredArgsConstructor
public class AuthControllerS {

    @Autowired
    private AuthServiceS authService;

    @Autowired
    private MemberMapper mapper;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "가입 신청 상태로 회원가입합니다.")
    public ResponseEntity<ResponseDto> signup(@Valid @RequestBody MemberCreateDto dto) {
        try {
            authService.signup(mapper.toModel(dto));
            return ResponseEntity.ok(ResponseDto.success("회원가입이 완료되었습니다. 관리자 승인 후 로그인이 가능합니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error(e.getMessage()));
        }
    }
}
