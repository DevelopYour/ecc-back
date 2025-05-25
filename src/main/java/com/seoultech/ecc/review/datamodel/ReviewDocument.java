package com.seoultech.ecc.review.datamodel;

import com.seoultech.ecc.global.BaseDocument;
import com.seoultech.ecc.member.dto.MemberSimpleDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "review")
public class ReviewDocument extends BaseDocument {
    @Id
    private String id;

    private String reportId;

    private int week;

    private MemberSimpleDto member;

    private ReviewStatus status;

    private String contents;
}
