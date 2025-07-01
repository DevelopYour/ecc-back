package com.seoultech.ecc.report.dto;

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
    private List<ReportFeedbackDto> feedbacks;
    private List<ReportTranslationDto> translations;
}
