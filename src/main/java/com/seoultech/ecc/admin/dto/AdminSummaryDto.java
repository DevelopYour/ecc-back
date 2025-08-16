package com.seoultech.ecc.admin.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminSummaryDto {
    private Long totalMembers;
    private Long pendingMembers;
    private Long regularTeams;
    private Long oneTimeTeams;
    private Long pendingReports;
}
