package com.seoultech.ecc.service;

import com.seoultech.ecc.dto.auth.LoginRequest;
import com.seoultech.ecc.dto.auth.SignupRequest;
import com.seoultech.ecc.dto.auth.TokenResponse;
import com.seoultech.ecc.dto.auth.TokenRefreshRequest;
import com.seoultech.ecc.dto.member.MemberResponse;
import com.seoultech.ecc.entity.MajorEntity;
import com.seoultech.ecc.entity.MemberEntity;
import com.seoultech.ecc.entity.MemberStatus;
import com.seoultech.ecc.repository.MajorRepository;
import com.seoultech.ecc.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final MajorRepository majorRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private static final String ROLE_USER = "ROLE_USER";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    @Transactional
    public MemberResponse signup(SignupRequest request) {
        // 이미 가입된 학번인지 확인
        if (memberRepository.findByStudentId(request.getStudentId()).isPresent()) {
            throw new RuntimeException("이미 가입된 학번입니다.");
        }

        // 전공 정보 조회
        MajorEntity major = majorRepository.findById(request.getMajorId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 전공입니다."));

        // 비밀번호 암호화 (초기 비밀번호는 전화번호)
        String hashedPassword = passwordEncoder.encode(request.getTel());

        // 회원 엔티티 생성
        MemberEntity member = MemberEntity.builder()
                .studentId(request.getStudentId())
                .name(request.getName())
                .tel(request.getTel())
                .kakaoId(request.getKakaoId())
                .password(hashedPassword) // 암호화된 비밀번호 설정
                .email(request.getEmail())
                .level(request.getLevel()) // 직접 전달된 레벨 사용
                .rate(0.0) // 초기 평점은 0으로 설정
                .status(MemberStatus.PENDING) // 가입 승인 대기 상태로 설정
                .major(major)
                .motivation(request.getMotivation())
                .role(ROLE_USER) // 기본 역할은 ROLE_USER
                .build();

        // 회원 저장
        MemberEntity savedMember = memberRepository.save(member);

        // 응답 DTO 변환 및 반환
        return MemberResponse.fromEntity(savedMember);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        try {
            // 학번으로 회원 조회
            MemberEntity member = memberRepository.findByStudentId(request.getStudentId())
                    .orElseThrow(() -> new RuntimeException("등록되지 않은 학번입니다."));

            // 회원 상태 검증
            if (member.getStatus() == MemberStatus.BANNED || member.getStatus() == MemberStatus.WITHDRAWN) {
                throw new RuntimeException("접근이 제한된 회원입니다.");
            }

            // Spring Security의 인증 처리
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getStudentId(), request.getPassword())
            );

            // 인증 정보 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 토큰 생성 및 반환
            return jwtService.generateTokens(member);

        } catch (BadCredentialsException e) {
            throw new RuntimeException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }
    }

    @Transactional
    public void logout(String studentId) {
        // Refresh Token 삭제
        jwtService.logout(studentId);

        // 현재 인증 정보 삭제
        SecurityContextHolder.clearContext();
    }

    @Transactional
    public TokenResponse refreshToken(TokenRefreshRequest request) {
        return jwtService.refreshAccessToken(request.getRefreshToken());
    }
}