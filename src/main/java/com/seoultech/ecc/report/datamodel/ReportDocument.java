package com.seoultech.ecc.report.datamodel;

import com.seoultech.ecc.global.BaseDocument;
import com.seoultech.ecc.member.dto.MemberSimpleDto;
import com.seoultech.ecc.report.dto.ReportTopicDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "report")
public class ReportDocument extends BaseDocument {
    @Id
    private String id;
    private Integer teamId;
    private Integer subjectId;
    private List<MemberSimpleDto> members;
    private int week;
    private List<ReportTopicDto> topics;
    private String comments;
    private int grade;
    private boolean submitted;
    private LocalDateTime submittedAt;
}
