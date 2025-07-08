package com.seoultech.ecc.admin.dto;

import com.seoultech.ecc.study.datamodel.TopicEntity;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicDetailDto {
    private Integer id;
    private Integer categoryId;
    private String categoryName;
    private String topic;
    private int usageCount;

    public static TopicDetailDto fromEntity(TopicEntity entity) {
        return TopicDetailDto.builder()
                .id(entity.getId())
                .categoryId(entity.getCategory().getId())
                .categoryName(entity.getCategory().getCategory())
                .topic(entity.getTopic())
                .usageCount(0) // TODO
                .build();
    }
}
