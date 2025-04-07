package com.seoultech.ecc.mapper;

import com.seoultech.ecc.domain.Member;
import com.seoultech.ecc.dto.MemberCreateDto;
import com.seoultech.ecc.dto.MemberDto;
import com.seoultech.ecc.dto.member.MemberResponse;
import com.seoultech.ecc.entity.MajorEntity;
import com.seoultech.ecc.entity.MemberEntity;
import com.seoultech.ecc.repository.MajorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {

    @Autowired
    private MajorRepository majorRepository;

    /**
     * MemberResponse DTO를 MemberEntity로 변환
     */
    public MemberEntity toEntity(MemberResponse dto) {
        MemberEntity entity = new MemberEntity();
        entity.setUuid(dto.getUuid());
        entity.setStudentId(dto.getStudentId());
        entity.setName(dto.getName());
        entity.setTel(dto.getTel());
        entity.setEmail(dto.getEmail());
        entity.setLevel(dto.getLevel());
        entity.setRate(dto.getRate());
        entity.setStatus(dto.getStatus());
        entity.setMotivation(dto.getMotivation());

        /* 카카오 로그인은 현재 미구현
        entity.setKakaoUuid(dto.getKakaoUuid());
        entity.setKakaoTel(dto.getKakaoTel());
        */

        // majorId가 있을 경우 MajorEntity 설정
        if (dto.getMajorId() != null) {
            majorRepository.findById(dto.getMajorId())
                    .ifPresent(entity::setMajor);
        }

        return entity;
    }

    /**
     * MemberEntity를 MemberResponse DTO로 변환
     * (MemberResponse.fromEntity 메서드가 이미 구현되어 있으므로 필요 시 참조)
     */
    public MemberResponse toDto(MemberEntity entity) {
        return MemberResponse.fromEntity(entity);
    }


    // DTO → Domain
    public Member toModel(MemberDto dto) {
        return Member.builder()
                .uuid(dto.getUuid())
//                .kakaoUuid(dto.getKakaoUuid())
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

    // CreateDTO → Domain
    public Member toModel(MemberCreateDto dto) {
        return Member.builder()
//                .kakaoUuid(dto.getKakaoUuid())
                .studentId(dto.getStudentId())
                .tel(dto.getTel())
                .kakaoTel(dto.getKakaoTel())
                .name(dto.getName())
                .email(dto.getEmail())
                .level(dto.getLevel())
                .majorId(dto.getMajorId())
                .build();
    }

    // Domain → Entity
    public MemberEntity toEntity(Member domain, MajorEntity major) {
        MemberEntity entity = new MemberEntity();
        entity.setUuid(domain.getUuid());
        entity.setStudentId(domain.getStudentId());
//        entity.setKakaoUuid(domain.getKakaoUuid());
        entity.setPassword(domain.getPassword());
        entity.setTel(domain.getTel());
        entity.setKakaoTel(domain.getKakaoTel());
        entity.setName(domain.getName());
        entity.setEmail(domain.getEmail());
        entity.setLevel(domain.getLevel());
        entity.setRate(domain.getRate());
        entity.setStatus(domain.getStatus());
        entity.setMajor(major);
        entity.setRole(domain.getRole());
        return entity;
    }

    // Entity → Domain
    public Member toDomain(MemberEntity entity) {
        return Member.builder()
                .uuid(entity.getUuid())
//                .kakaoUuid(entity.getKakaoUuid())
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
                .role(entity.getRole())
                .build();
    }

    // Domain → DTO
    public MemberDto toDto(Member domain) {
        return MemberDto.builder()
                .uuid(domain.getUuid())
//                .kakaoUuid(domain.getKakaoUuid())
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
                .role(domain.getRole())
                .build();
    }
}