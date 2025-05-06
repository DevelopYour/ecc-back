package com.seoultech.ecc.member.dto;

import com.seoultech.ecc.member.datamodel.MemberEntity;
import com.seoultech.ecc.member.datamodel.MemberStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final MemberEntity memberEntity;

    public CustomUserDetails(MemberEntity memberEntity) {
        this.memberEntity = memberEntity;
    }

    // 사용자의 권한 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // 기본 역할 추가 (ROLE_USER 또는 ROLE_ADMIN)
        authorities.add(new SimpleGrantedAuthority(memberEntity.getRole()));

        return authorities;
    }

    /**
     * UUID 반환 (memberEntity.getUuid())
     * 회원 식별 시 항상 이 메서드를 사용
     */
    public Integer getId() {
        return memberEntity.getUuid();
    }

    @Override
    public String getPassword() {
        return memberEntity.getPassword();
    }

    /**
     * 로그인 ID(학번) 반환
     * Security 인증에만 사용되고 엔티티 참조에는 사용하지 않음
     */
    @Override
    public String getUsername() {
        return memberEntity.getStudentId();
    }

    /**
     * 멤버 이름 반환 (표시용)
     */
    public String getName() {
        return memberEntity.getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return memberEntity.getStatus() != MemberStatus.BANNED
                && memberEntity.getStatus() != MemberStatus.SUSPENDED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // PENDING 상태와 ACTIVE 상태 모두 로그인 허용
        return memberEntity.getStatus() == MemberStatus.ACTIVE ||
                memberEntity.getStatus() == MemberStatus.PENDING;
    }
}