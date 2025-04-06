package com.seoultech.ecc.mapper;

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
}