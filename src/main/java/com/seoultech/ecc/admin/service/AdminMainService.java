package com.seoultech.ecc.admin.service;


import com.seoultech.ecc.admin.dto.AdminSummaryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminMainService {

    private final AdminMemberService memberService;
    private final AdminTeamService teamService;

    @Transactional
    public AdminSummaryDto getSummary() {
        AdminSummaryDto summary = new AdminSummaryDto();
        summary.setTotalMembers(memberService.countAllMembers());
        summary.setPendingMembers(memberService.countAllPendingMembers());
        summary.setRegularTeams(teamService.countTeams(true));
        summary.setOneTimeTeams(teamService.countTeams(false));
        summary.setPendingReports(teamService.countUncheckedReports());
        return summary;
    }
}
