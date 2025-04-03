package com.seoultech.ecc.service;

import com.seoultech.ecc.domain.Member;
import com.seoultech.ecc.entity.MajorEntity;
import com.seoultech.ecc.entity.MemberEntity;
import com.seoultech.ecc.entity.MemberStatus;
import com.seoultech.ecc.mapper.MemberMapper;
import com.seoultech.ecc.repository.MajorRepository;
import com.seoultech.ecc.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private MemberMapper mapper;

    public Member signup(Member member) {
        member.setStatus(MemberStatus.PENDING);
        MemberEntity entity = memberRepository.save(this.toEntity(member));
        return mapper.toDomain(entity);
    }

    private MemberEntity toEntity(Member member) {
        MajorEntity major = majorRepository.findById(member.getMajorId()).get();
//                .orElseThrow(() -> new NotFoundException("과목 없음"));
        return mapper.toEntity(member, major);
    }
}
