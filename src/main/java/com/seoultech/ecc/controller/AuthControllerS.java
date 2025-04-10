package com.seoultech.ecc.controller;

import com.seoultech.ecc.dto.MemberCreateDto;
import com.seoultech.ecc.dto.ResponseDto;
import com.seoultech.ecc.mapper.MemberMapper;
import com.seoultech.ecc.service.AuthServiceS;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth-s")
@RequiredArgsConstructor
public class AuthControllerS {

    @Autowired
    private AuthServiceS authService;

    @Autowired
    private MemberMapper mapper;

    @PostMapping("/signup")
    public ResponseEntity<ResponseDto> signup(@Valid @RequestBody MemberCreateDto dto) {
        try {
            authService.signup(mapper.toModel(dto));
            return ResponseEntity.ok(ResponseDto.success(null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error(null));
        }
    }

}
