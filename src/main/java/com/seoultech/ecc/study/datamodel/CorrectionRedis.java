package com.seoultech.ecc.study.datamodel;

import com.seoultech.ecc.report.dto.CorrectionDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CorrectionRedis {
    private String id;
    private String question; // 문제
    private String answer; // 정답
    private String description; // 설명

    public CorrectionDto toCorrectionDto(){
        return CorrectionDto.builder()
                .question(question)
                .answer(answer)
                .description(description)
                .build();
    }
}

