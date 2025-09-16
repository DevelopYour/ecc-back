package com.seoultech.ecc.study.datamodel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeneralRedis {
    private String id;
    private List<CorrectionRedis> corrections; // 오답
    private List<VocabRedis> vocabs; // 단어
    private List<ExpressionRedis> expressions; // 표현
}

