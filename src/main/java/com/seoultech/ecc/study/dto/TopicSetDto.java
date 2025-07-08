package com.seoultech.ecc.study.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicSetDto {
    private Integer id;
    private String category;
    private String description;
    private List<TopicDto> topics;
}
