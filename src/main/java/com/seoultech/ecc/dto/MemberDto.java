package com.seoultech.ecc.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDto {
    private Long uuid;
    private String kakaoUuid;
    private String userId;
    private String name;
    private String studentNo;
    private String email;
    private int level;
    private double rate;
    private Long subjectId;
}
