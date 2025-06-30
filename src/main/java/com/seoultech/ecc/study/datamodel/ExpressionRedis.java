package com.seoultech.ecc.study.datamodel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpressionRedis {
    private Long expressionId;
    private String question;
    private String english;
    private String korean;
    private String example;
    private String feedback;
    public boolean isTranslation;
}
