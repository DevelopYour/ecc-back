package com.seoultech.ecc.study.dto;

import com.seoultech.ecc.study.datamodel.TopicCategoryEntity;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicSetDto {
    private Long id;
    private String category;
    private String description;
    private List<TopicDto> topics;
}
