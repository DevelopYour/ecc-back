package com.seoultech.ecc.study.dto;

import com.seoultech.ecc.study.datamodel.TopicCategory;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicDto {
    private TopicCategory category;
    private String topic;
}
