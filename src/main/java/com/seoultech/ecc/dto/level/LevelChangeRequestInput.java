package com.seoultech.ecc.dto.level;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LevelChangeRequestInput {

    @NotNull(message = "요청 레벨은 필수 선택 항목입니다.")
    private Integer requestedLevel;

    private String reason;
}