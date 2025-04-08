package com.seoultech.ecc.service;

import com.seoultech.ecc.dto.auth.LoginRequest;
import com.seoultech.ecc.dto.auth.LoginResponse;
import com.seoultech.ecc.dto.auth.SignupRequest;
import com.seoultech.ecc.dto.member.MemberResponse;
import com.seoultech.ecc.entity.College;
import com.seoultech.ecc.entity.MajorEntity;
import com.seoultech.ecc.entity.MemberEntity;
import com.seoultech.ecc.entity.MemberStatus;
import com.seoultech.ecc.repository.MajorRepository;
import com.seoultech.ecc.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final MajorRepository majorRepository;

    // 임시 토큰 저장소 (실제로는 Redis나 DB 사용 권장)
    private static final Map<String, Integer> tokenStore = new HashMap<>();
    private static final String SESSION_USER_KEY = "LOGIN_USER_ID";

    @Transactional
    public MemberResponse signup(SignupRequest request) {
        // 이미 가입된 학번인지 확인
        if (memberRepository.findByStudentId(request.getStudentId()).isPresent()) {
            throw new RuntimeException("이미 가입된 학번입니다.");
        }

        // 기본 전공 설정 (임시 방법)
        MajorEntity major;
        try {
            // 학과 ID로 조회 시도
            major = majorRepository.findById(request.getMajorId())
                    .orElseThrow(() -> new RuntimeException(""));
        } catch (Exception e) {
            // 학과 조회 실패 시 기본 전공 생성
            major = new MajorEntity();
            major.setName("기술경영학과");
            major.setCollege(College.BUSINESS);
            major = majorRepository.save(major);
        }

        // 전공이 존재하는지 확인
        /*MajorEntity major = majorRepository.findById(request.getMajorId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 전공입니다."));*/

        // 영어 실력 레벨 변환 (초급=1, 중급=2, 고급=3)
        int level = 1; // 기본값 초급
        if ("중급".equals(request.getLevel())) {
            level = 2;
        } else if ("고급".equals(request.getLevel())) {
            level = 3;
        }

        // 회원 엔티티 생성
        MemberEntity member = MemberEntity.builder()
                .studentId(request.getStudentId())
                .name(request.getName())
                .tel(request.getTel())
                .password(request.getTel()) // 초기 비밀번호는 전화번호와 동일하게 설정
                .email(request.getEmail())
                .level(level)
                .rate(0.0) // 초기 평점은 0으로 설정
                .status(MemberStatus.PENDING) // 가입 승인 대기 상태로 설정
                .major(major)
                .motivation(request.getMotivation())
                .build();

        // 회원 저장
        MemberEntity savedMember = memberRepository.save(member);

        // 응답 DTO 변환 및 반환
        return MemberResponse.fromEntity(savedMember);

    }

    public LoginResponse login(LoginRequest request, HttpSession session) {
        // 학번으로 회원 조회
        MemberEntity member = memberRepository.findByStudentId(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("등록되지 않은 학번입니다."));

        // 비밀번호 검증
        if (!member.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 회원 상태 검증
        if (member.getStatus() == MemberStatus.BANNED || member.getStatus() == MemberStatus.WITHDRAWN) {
            throw new RuntimeException("접근이 제한된 회원입니다.");
        }

        // 토큰 생성 (실제로는 JWT 사용)
        String token = UUID.randomUUID().toString();
        tokenStore.put(token, member.getUuid());

        // 세션에 사용자 ID 저장
        session.setAttribute(SESSION_USER_KEY, member.getUuid());

        // 로그인 응답 생성
        return LoginResponse.builder()
                .token(token)
                .uuid(member.getUuid())
                .studentId(member.getStudentId())
                .name(member.getName())
                .status(member.getStatus())
                .build();
    }

    @Transactional
    public void logout(String token, HttpSession session) {
        // 토큰 저장소에서 제거
        if (token != null && !token.isEmpty()) {
            tokenStore.remove(token);
        }

        // 세션 무효화
        session.invalidate();
    }
}
