package com.seoultech.ecc.study.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicDto {
    private Integer id;
    private String category;
    private String topic;
}
