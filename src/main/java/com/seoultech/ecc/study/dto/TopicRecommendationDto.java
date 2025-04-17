package com.seoultech.ecc.study.dto;

import com.seoultech.ecc.study.datamodel.TopicCategory;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicRecommendationDto {
    private TopicCategory category;
    private List<String> topic;
}
