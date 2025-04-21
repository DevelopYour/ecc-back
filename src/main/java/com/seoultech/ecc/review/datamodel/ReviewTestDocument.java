package com.seoultech.ecc.review.datamodel;

import com.seoultech.ecc.review.dto.QuestionDto;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "report")
public class ReviewTestDocument {
    @Id
    private String id;
    private Integer userId;
    private List<QuestionDto> questions;
    private boolean isComplete;
}
