package com.seoultech.ecc.admin.service;

import com.seoultech.ecc.member.datamodel.MemberStatus;
import com.seoultech.ecc.team.dto.ApplyStudyDto;
import com.seoultech.ecc.team.service.ApplyStudyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminTeamMatchService {

    private final ApplyStudyService applyStudyService;
    private final AdminMemberService adminMemberService;

    public List<ApplyStudyDto.ApplyResponse> getRegularApplicants() {
        return adminMemberService.getMembersByStatus(MemberStatus.ACTIVE)
                .stream()
                .map(member -> applyStudyService.getRegularStudyApplications(member.getUuid()))
                .filter(Objects::nonNull) // 신청 내역 없으면 제외
                .collect(Collectors.toList());
    }

}
