package com.seoultech.ecc.member.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberSimpleDto {
    private Integer id;
    private String name;
}
