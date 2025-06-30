package com.seoultech.ecc.report.dto;

import com.seoultech.ecc.study.datamodel.TopicRedis;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportTopicDto {
    private String category;
    private String topic;
    private List<ReportExpressionDto> expressions;
}
