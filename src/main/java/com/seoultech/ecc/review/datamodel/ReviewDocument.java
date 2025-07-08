package com.seoultech.ecc.review.datamodel;

import com.seoultech.ecc.global.BaseDocument;
import com.seoultech.ecc.member.dto.MemberSimpleDto;
import com.seoultech.ecc.report.datamodel.ReportDocument;
import com.seoultech.ecc.report.dto.ReportTopicDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "review")
public class ReviewDocument extends BaseDocument {
    @Id
    private String id;
    private MemberSimpleDto member;
    private String reportId;
    private Integer teamId;
    private Integer subjectId;
    private int week;
    private List<ReportTopicDto> topics;
    private ReviewStatus status;

    public static ReviewDocument fromReport(ReportDocument report){
       // member & status: 서비스 로직에서 처리
        return ReviewDocument.builder()
                .reportId(report.getId())
                .teamId(report.getTeamId())
                .subjectId(report.getSubjectId())
                .week(report.getWeek())
                .topics(report.getTopics())
                .build();
    }
}
