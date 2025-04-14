package com.seoultech.ecc.member.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MajorDto {
    private Long majorId;
    private String name;
    private String college;
}
