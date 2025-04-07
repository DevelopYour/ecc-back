package com.seoultech.ecc.service;

import com.seoultech.ecc.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JoinService {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

//    public void joinProcess(JoinDto joinDto) {
//
//        // username 중복 검사
//        boolean isUser = memberRepository.existsByUsername(joinDto.getUsername());
//        if (isUser) return;
//
//        MemberEntity data = new MemberEntity();
//        data.setStudentNo(joinDto.getUsername());
//        data.setPassword(bCryptPasswordEncoder.encode(joinDto.getPassword())); // 비밀번호 암호화
//        data.setRole("ROLE_USER");
//        memberRepository.save(data);
//    }
}
