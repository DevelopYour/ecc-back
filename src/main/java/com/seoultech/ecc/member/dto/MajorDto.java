package com.seoultech.ecc.member.dto;

import com.seoultech.ecc.member.datamodel.MajorEntity;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MajorDto {
    private Integer id; // Long → Integer 변경
    private String name;
    private String college;

    public static MajorDto fromEntity(MajorEntity entity) {
        return MajorDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .college(entity.getCollege().toString())
                .build();
    }
}