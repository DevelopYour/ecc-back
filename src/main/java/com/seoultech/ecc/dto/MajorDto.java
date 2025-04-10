package com.seoultech.ecc.dto;

import com.seoultech.ecc.entity.MajorEntity;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MajorDto {
    private Long id;
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
