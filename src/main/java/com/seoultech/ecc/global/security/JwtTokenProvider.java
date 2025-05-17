package com.seoultech.ecc.global.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token.expiration}")
    private long accessTokenValidityInMilliseconds;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenValidityInMilliseconds;

    private Key key;

    private final UserDetailsService userDetailsService;

    public JwtTokenProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    protected void init() {
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        key = Keys.hmacShaKeyFor(encodedKey.getBytes());
    }

    // AccessToken 생성 - uuid 추가
    public String createAccessToken(String username, Integer uuid, String role) {
        return createToken(username, uuid, role, accessTokenValidityInMilliseconds);
    }

    // RefreshToken 생성 - uuid 추가
    public String createRefreshToken(String username, Integer uuid) {
        return createToken(username, uuid, null, refreshTokenValidityInMilliseconds);
    }

    // 토큰 생성 공통 메서드 - uuid 추가
    private String createToken(String username, Integer uuid, String role, long validityInMilliseconds) {
        Claims claims = Jwts.claims().setSubject(username);
        // uuid 추가
        claims.put("uuid", uuid);
        if (role != null) {
            claims.put("role", role);
        }

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰에서 인증 정보 추출
    public Authentication getAuthentication(String token) {
        // 토큰에서 UUID 추출
        Integer uuid = getUuid(token);

        // 사용자 이름으로 UserDetails 로드
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(getUsername(token));

        // Authentication 객체를 생성할 때 UUID를 credentials에 설정
        return new UsernamePasswordAuthenticationToken(userDetails, uuid, userDetails.getAuthorities());
    }

    // 토큰에서 사용자명 추출
    public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 토큰에서 UUID 추출
    public Integer getUuid(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("uuid", Integer.class);
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 토큰에서 만료일 추출
    public Date getExpirationDate(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    // HTTP 요청에서 토큰 추출
    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}