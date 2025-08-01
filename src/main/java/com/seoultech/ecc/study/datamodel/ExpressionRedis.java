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
    private Integer expressionId;
    private String english;
    private String korean;
    private String exampleEnglish; // 번역 only
    private String exampleKorean; // 번역 only
    private String original; // 교정 only
    private String feedback; // 교정 only
    public boolean translation;
}
