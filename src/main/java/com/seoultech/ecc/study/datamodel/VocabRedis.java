package com.seoultech.ecc.study.datamodel;

import com.seoultech.ecc.report.dto.VocabDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VocabRedis {
    private String id;
    private String english;
    private String korean;

    public VocabDto toVocabDto() {
        return VocabDto.builder()
                .english(english)
                .korean(korean)
                .build();
    }
}

