package com.seoultech.ecc.admin.service;

import com.seoultech.ecc.admin.dto.AssignedTeamDto;
import com.seoultech.ecc.member.datamodel.MemberEntity;
import com.seoultech.ecc.member.datamodel.MemberStatus;
import com.seoultech.ecc.member.dto.MemberSimpleDto;
import com.seoultech.ecc.member.service.MemberService;
import com.seoultech.ecc.team.datamodel.*;
import com.seoultech.ecc.team.dto.ApplyStudyDto;
import com.seoultech.ecc.team.repository.ApplyRegularSubjectRepository;
import com.seoultech.ecc.team.repository.ApplyRegularTimeRepository;
import com.seoultech.ecc.team.repository.TeamRepository;
import com.seoultech.ecc.team.service.ApplyStudyService;
import com.seoultech.ecc.team.service.SubjectService;
import com.seoultech.ecc.team.service.TimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminTeamMatchService {

    private final ApplyRegularTimeRepository applyTimeRepository;
    private final ApplyRegularSubjectRepository applySubjectRepository;
    private final TeamRepository teamRepository;

    private final ApplyStudyService applyStudyService;
    private final AdminMemberService adminMemberService;
    private final TimeService timeService;
    private final SubjectService subjectService;

    private final TeamAssignmentOptimizer optimizer;
    private final MemberService memberService;

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
            memberSubjectMap.computeIfAbsent(app.getMember().getId(), k -> new ArrayList<>())
                    .add(app.getSubject().getId());
        }

        // Build member-time mapping
        Map<Integer, List<Integer>> memberTimeMap = new HashMap<>();
        List<ApplyRegularTimeEntity> timeApplications = applyTimeRepository.findAll();

        for (ApplyRegularTimeEntity app : timeApplications) {
            memberTimeMap.computeIfAbsent(app.getMember().getId(), k -> new ArrayList<>())
                    .add(app.getTime().getId());
        }


        Map<Integer, Integer> timeHourMap = timeService.getTimeHourMap();
        Map<Integer, SubjectEntity> subjectEntityMap = subjectService.getSubjectMap();

        // Run optimization
        return optimizer.optimizeTeamAssignment(members, memberSubjectMap, memberTimeMap, timeHourMap, subjectEntityMap);
    }

    @Transactional
    public Integer saveTeams(List<AssignedTeamDto> results) {
        results.forEach(this::fromAssignedTeamDtoToTeamEntity);
        return results.size();
    }

    @Transactional
    protected void fromAssignedTeamDtoToTeamEntity(AssignedTeamDto dto) {
        TeamEntity entity = new TeamEntity();
        SubjectEntity subject = subjectService.getSubjectById(dto.getSubjectId());
        TimeEntity time = timeService.getTimeById(dto.getTimeId());
        entity.setSubject(subject);
        entity.setTime(time);
        entity.setName(subject.getName() + "(" + time.getDay() + "-" + time.getStartTime() + "시)");
        entity.setScore(0);
        // TODO: 학기
        entity.setRegular(true);
        entity.setStudyCount(0);

        // 먼저 TeamEntity만 저장해서 ID 생성
        TeamEntity savedTeam = teamRepository.save(entity);

        // 이제 저장된 Team으로 TeamMember 생성
        List<TeamMemberEntity> teamMembers = new ArrayList<>();
        for(MemberSimpleDto member : dto.getMembers()) {
            MemberEntity memberEntity = memberService.getMemberByUuid(member.getId());

            TeamMemberEntity teamMember = new TeamMemberEntity();
            teamMember.setMember(memberEntity);
            teamMember.setTeam(savedTeam);  // 저장된 Team 사용
            teamMembers.add(teamMember);
        }

        savedTeam.setTeamMembers(teamMembers);
        teamRepository.save(savedTeam);  // 최종 저장
    }

}
