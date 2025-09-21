package com.seoultech.ecc.report.datamodel;

import com.seoultech.ecc.global.BaseDocument;
import com.seoultech.ecc.member.dto.MemberSimpleDto;
import com.seoultech.ecc.report.dto.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Document(collection = "report")
public class ReportDocument extends BaseDocument {
    @Id
    private String id;
    private Integer teamId;
    private Integer subjectId;
    private List<MemberSimpleDto> members;
    private int week;
    private Integer grade;
    private boolean submitted;
    private LocalDateTime submittedAt;

    private List<ReportTopicDto> topics; // speaking

    private List<CorrectionDto> corrections; // general
    private List<VocabDto> vocabs; // general
    private List<ReportTranslationDto> translations; // general
    private List<ReportFeedbackDto> feedbacks; // general
}
