package com.seoultech.ecc.mapper;

import com.seoultech.ecc.domain.Member;
import com.seoultech.ecc.dto.MemberDto;
import com.seoultech.ecc.entity.MajorEntity;
import com.seoultech.ecc.entity.MemberEntity;
import org.springframework.stereotype.Component;

// Major: majorId, majorName
@Component
public class MemberMapper {

    // DTO → Domain
    public Member toModel(MemberDto dto) {
        return Member.builder()
                .uuid(dto.getUuid())
                .kakaoUuid(dto.getKakaoUuid())
                .password(dto.getPassword())
                .tel(dto.getTel())
                .kakaoTel(dto.getKakaoTel())
                .name(dto.getName())
                .email(dto.getEmail())
                .level(dto.getLevel())
                .rate(dto.getRate())
                .status(dto.getStatus())
                .majorId(dto.getMajorId())
                .majorName(dto.getMajorName())
                .build();
    }

    // Domain → Entity
    public MemberEntity toEntity(Member domain, MajorEntity major) {
        MemberEntity entity = new MemberEntity();
        entity.setUuid(domain.getUuid());
        entity.setKakaoUuid(domain.getKakaoUuid());
        entity.setPassword(domain.getPassword());
        entity.setTel(domain.getTel());
        entity.setKakaoTel(domain.getKakaoTel());
        entity.setName(domain.getName());
        entity.setEmail(domain.getEmail());
        entity.setLevel(domain.getLevel());
        entity.setRate(domain.getRate());
        entity.setStatus(domain.getStatus());
        entity.setMajor(major);
        return entity;
    }

    // Entity → Domain
    public Member toDomain(MemberEntity entity) {
        return Member.builder()
                .uuid(entity.getUuid())
                .kakaoUuid(entity.getKakaoUuid())
                .password(entity.getPassword())
                .tel(entity.getTel())
                .kakaoTel(entity.getKakaoTel())
                .name(entity.getName())
                .email(entity.getEmail())
                .level(entity.getLevel())
                .rate(entity.getRate())
                .status(entity.getStatus())
                .majorId(entity.getMajor().getId())
                .majorName(entity.getMajor().getName())
                .build();
    }

    // Domain → DTO
    public MemberDto toDto(Member domain) {
        return MemberDto.builder()
                .uuid(domain.getUuid())
                .kakaoUuid(domain.getKakaoUuid())
                .password(domain.getPassword())
                .tel(domain.getTel())
                .kakaoTel(domain.getKakaoTel())
                .name(domain.getName())
                .email(domain.getEmail())
                .level(domain.getLevel())
                .rate(domain.getRate())
                .status(domain.getStatus())
                .majorId(domain.getMajorId())
                .majorName(domain.getMajorName())
                .build();
    }
}

