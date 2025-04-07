package com.seoultech.ecc.service;


import com.seoultech.ecc.dto.CustomUserDetails;
import com.seoultech.ecc.entity.MemberEntity;
import com.seoultech.ecc.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// 로그인 시 SecurityConfig에게 전달
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String studentNo) throws UsernameNotFoundException {

        MemberEntity memberData = memberRepository.findByStudentNo(studentNo);
        if (memberData != null) {
            return new CustomUserDetails(memberData);
        }
        return null;
    }
}
