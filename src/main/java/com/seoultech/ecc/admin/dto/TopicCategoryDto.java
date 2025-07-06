package com.seoultech.ecc.admin.dto;

import com.seoultech.ecc.study.datamodel.TopicCategoryEntity;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicCategoryDto {
    private Long id;
    private String name;
    private String description;

    public static TopicCategoryDto fromEntity(TopicCategoryEntity entity){
        return TopicCategoryDto.builder()
                .id(entity.getId())
                .name(entity.getCategory())
                .description(entity.getDescription())
                .build();
    }
}
