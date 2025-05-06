package com.seoultech.ecc.team.service;

import com.seoultech.ecc.member.datamodel.MemberEntity;
import com.seoultech.ecc.member.repository.MemberRepository;
import com.seoultech.ecc.team.datamodel.TeamEntity;
import com.seoultech.ecc.team.dto.TeamDto;
import com.seoultech.ecc.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;

    /**
     * 회원이 속한 모든 팀 조회
     */
    @Transactional(readOnly = true)
    public List<TeamDto> getTeamsByMember(Integer uuid) {
        List<TeamEntity> teams = teamRepository.findTeamsByMember(
                uuid,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return teams.stream()
                .map(TeamDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 회원이 속한 정규 스터디 팀 조회
     */
    @Transactional(readOnly = true)
    public List<TeamDto> getRegularTeamsByMember(Integer uuid) {
        List<TeamEntity> allTeams = teamRepository.findTeamsByMember(
                uuid,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return allTeams.stream()
                .filter(TeamEntity::isRegular)
                .map(TeamDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 회원이 속한 번개 스터디 팀 조회
     */
    @Transactional(readOnly = true)
    public List<TeamDto> getOneTimeTeamsByMember(Integer uuid) {
        List<TeamEntity> allTeams = teamRepository.findTeamsByMember(
                uuid,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return allTeams.stream()
                .filter(team -> !team.isRegular())
                .map(TeamDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 팀 상세 정보 조회
     */
    @Transactional(readOnly = true)
    public TeamDto getTeamDetail(Long teamId, String studentId) {
        TeamEntity team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 팀입니다. ID: " + teamId));

        return TeamDto.fromEntityWithDetails(team, studentId);
    }

    /**
     * 회원이 해당 팀에 속해있는지 확인
     */
    @Transactional(readOnly = true)
    public boolean isTeamMember(Long teamId, Integer uuid) {
        TeamEntity team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 팀입니다. ID: " + teamId));

        return team.getTeamMembers().stream()
                .anyMatch(tm -> tm.getMember().getUuid().equals(uuid));
    }
}