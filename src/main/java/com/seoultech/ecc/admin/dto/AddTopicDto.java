package com.seoultech.ecc.admin.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddTopicDto {
    private Integer categoryId;
    private String topic;
}
