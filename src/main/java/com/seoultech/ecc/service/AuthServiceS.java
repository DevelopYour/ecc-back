package com.seoultech.ecc.service;

import com.seoultech.ecc.domain.Member;
import com.seoultech.ecc.entity.MajorEntity;
import com.seoultech.ecc.entity.MemberStatus;
import com.seoultech.ecc.mapper.MemberMapper;
import com.seoultech.ecc.repository.MajorRepository;
import com.seoultech.ecc.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceS {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private MemberMapper mapper;

    @Autowired
    private MajorRepository majorRepository;

    public void signup(Member member) {
        if (memberRepository.existsByStudentId(member.getStudentId())) return;
        member.setPassword(bCryptPasswordEncoder.encode(member.getPassword()));
        member.setRole("ROLE_USER");
        member.setStatus(MemberStatus.PENDING);
        member.setRate(0.0);
        memberRepository.save(mapper.toEntity(member, getMajor(member.getMajorId())));
    }


    private MajorEntity getMajor(Long majorId) {
        return majorRepository.findById(majorId).orElse(null);
    }

}
