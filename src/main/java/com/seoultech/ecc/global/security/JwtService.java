package com.seoultech.ecc.global.security;

import com.seoultech.ecc.member.dto.TokenResponse;
import com.seoultech.ecc.member.datamodel.MemberEntity;
import com.seoultech.ecc.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    /**
     * 로그인 성공 시 토큰 발급
     */
    @Transactional
    public TokenResponse generateTokens(MemberEntity member) {
        // Access Token 생성 - uuid 추가
        String accessToken = jwtTokenProvider.createAccessToken(member.getStudentId(), member.getId(), member.getRole());

        // Refresh Token 생성 - uuid 추가
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getStudentId(), member.getId());

        // Access Token 만료 시간 계산
        Date expirationDate = jwtTokenProvider.getExpirationDate(accessToken);
        long expiresIn = expirationDate.getTime() - new Date().getTime();

        // Refresh Token 만료 시간 계산
        Date refreshTokenExpirationDate = jwtTokenProvider.getExpirationDate(refreshToken);
        LocalDateTime expiryDateTime = refreshTokenExpirationDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        // 회원 엔티티에 Refresh Token 저장
        member.setRefreshToken(refreshToken);
        member.setRefreshTokenExpiresAt(expiryDateTime);
        memberRepository.save(member);

        // 토큰 응답 생성
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .accessTokenExpiresIn(expiresIn)
                .uuid(member.getId())
                .studentId(member.getStudentId())
                .name(member.getName())
                .status(member.getStatus())
                .role(member.getRole())
                .build();
    }

    /**
     * Refresh Token을 검증하고 새 Access Token 발급
     */
    @Transactional
    public TokenResponse refreshAccessToken(String refreshToken) {
        // Refresh Token 유효성 검사
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("유효하지 않은 리프레시 토큰입니다.");
        }

        // 사용자 학번 및 UUID 추출
        String studentId = jwtTokenProvider.getUsername(refreshToken);
        Integer uuid = jwtTokenProvider.getUuid(refreshToken);

        // 회원 정보 조회 - uuid 사용
        MemberEntity member = memberRepository.findById(uuid)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다. UUID: " + uuid));

        // 저장된 Refresh Token과 일치하는지 확인
        if (member.getRefreshToken() == null ||
                !member.getRefreshToken().equals(refreshToken)) {
            throw new RuntimeException("저장된 리프레시 토큰과 일치하지 않습니다.");
        }

        // 토큰 만료 여부 확인
        if (member.isRefreshTokenExpired()) {
            member.setRefreshToken(null);
            member.setRefreshTokenExpiresAt(null);
            memberRepository.save(member);
            throw new RuntimeException("만료된 리프레시 토큰입니다.");
        }

        // 새 Access Token 생성
        String newAccessToken = jwtTokenProvider.createAccessToken(studentId, member.getId(), member.getRole());

        // Access Token 만료 시간 계산
        Date expirationDate = jwtTokenProvider.getExpirationDate(newAccessToken);
        long expiresIn = expirationDate.getTime() - new Date().getTime();

        // 토큰 응답 생성 (Refresh Token은 재사용)
        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken) // 기존 리프레시 토큰 유지
                .tokenType("Bearer")
                .accessTokenExpiresIn(expiresIn)
                .uuid(member.getId())
                .studentId(member.getStudentId())
                .name(member.getName())
                .status(member.getStatus())
                .role(member.getRole())
                .build();
    }

    /**
     * 로그아웃 처리 (Refresh Token 제거)
     */
    @Transactional
    public void logout(Integer uuid) {
        MemberEntity member = memberRepository.findById(uuid)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        // Refresh Token 제거
        member.setRefreshToken(null);
        member.setRefreshTokenExpiresAt(null);
        memberRepository.save(member);
    }

    /**
     * 만료된 Refresh Token 정리 (매일 자정에 실행)
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        List<MemberEntity> membersWithExpiredTokens = memberRepository.findAll().stream()
                .filter(member -> member.getRefreshTokenExpiresAt() != null &&
                        member.getRefreshTokenExpiresAt().isBefore(now))
                .toList();

        for (MemberEntity member : membersWithExpiredTokens) {
            member.setRefreshToken(null);
            member.setRefreshTokenExpiresAt(null);
        }

        if (!membersWithExpiredTokens.isEmpty()) {
            memberRepository.saveAll(membersWithExpiredTokens);
        }
    }
}