package com.seoultech.ecc.study.dto;

import com.seoultech.ecc.study.datamodel.StudyStatus;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudySummaryDto {

    private Long teamId;

    private int week;

    private StudyStatus studyStatus;
}

