package com.seoultech.ecc.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationDto {
    private Long id;
    private Long memberId;
    private String contents;
    private boolean checked;
}