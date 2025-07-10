package com.seoultech.ecc.admin.service;

import com.seoultech.ecc.admin.dto.AssignedTeamDto;
import com.seoultech.ecc.member.datamodel.MemberStatus;
import com.seoultech.ecc.member.dto.MemberSimpleDto;
import com.seoultech.ecc.team.datamodel.ApplyRegularSubjectEntity;
import com.seoultech.ecc.team.datamodel.ApplyRegularTimeEntity;
import com.seoultech.ecc.team.datamodel.SubjectEntity;
import com.seoultech.ecc.team.dto.ApplyStudyDto;
import com.seoultech.ecc.team.repository.ApplyRegularSubjectRepository;
import com.seoultech.ecc.team.repository.ApplyRegularTimeRepository;
import com.seoultech.ecc.team.service.ApplyStudyService;
import com.seoultech.ecc.team.service.SubjectService;
import com.seoultech.ecc.team.service.TimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminTeamMatchService {

    private final ApplyRegularTimeRepository applyTimeRepository;
    private final ApplyRegularSubjectRepository applySubjectRepository;

    private final ApplyStudyService applyStudyService;
    private final AdminMemberService adminMemberService;
    private final TimeService timeService;
    private final SubjectService subjectService;

    private final TeamAssignmentOptimizer optimizer;

    public List<ApplyStudyDto.ApplyResponse> getRegularApplicants() {
        return adminMemberService.getMembersByStatus(MemberStatus.ACTIVE)
                .stream()
                .map(member -> applyStudyService.getRegularStudyApplications(member.getUuid()))
                .filter(Objects::nonNull) // 신청 내역 없으면 제외
                .collect(Collectors.toList());
    }

    public List<AssignedTeamDto> assignTeams() {
        // Prepare data
        List<MemberSimpleDto> members = adminMemberService.getAllMemberSimpleDto();

        // Build member-subject mapping
        Map<Integer, List<Integer>> memberSubjectMap = new HashMap<>();
        List<ApplyRegularSubjectEntity> subjectApplications = applySubjectRepository.findAll();

        for (ApplyRegularSubjectEntity app : subjectApplications) {
            memberSubjectMap.computeIfAbsent(app.getMember().getUuid(), k -> new ArrayList<>())
                    .add(app.getSubject().getSubjectId());
        }

        // Build member-time mapping
        Map<Integer, List<Integer>> memberTimeMap = new HashMap<>();
        List<ApplyRegularTimeEntity> timeApplications = applyTimeRepository.findAll();

        for (ApplyRegularTimeEntity app : timeApplications) {
            memberTimeMap.computeIfAbsent(app.getMember().getUuid(), k -> new ArrayList<>())
                    .add(app.getTime().getStartTime());
        }


        Map<Integer, Integer> timeHourMap = timeService.getTimeHourMap();
        Map<Integer, SubjectEntity> subjectEntityMap = subjectService.getSubjectMap();

        // Run optimization
        return optimizer.optimizeTeamAssignment(members, memberSubjectMap, memberTimeMap, timeHourMap, subjectEntityMap);
    }

}
