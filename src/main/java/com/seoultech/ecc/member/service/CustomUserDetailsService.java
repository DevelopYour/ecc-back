package com.seoultech.ecc.member.service;

import com.seoultech.ecc.member.dto.CustomUserDetails;
import com.seoultech.ecc.member.datamodel.MemberEntity;
import com.seoultech.ecc.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Autowired
    public CustomUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String studentId) throws UsernameNotFoundException {
        MemberEntity memberEntity = memberRepository.findByStudentId(studentId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + studentId));

        return new CustomUserDetails(memberEntity);
    }
}