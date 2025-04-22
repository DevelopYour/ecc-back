package com.seoultech.ecc.admin.service;

import com.seoultech.ecc.member.datamodel.MemberEntity;
import com.seoultech.ecc.member.repository.MemberRepository;
import com.seoultech.ecc.team.datamodel.TeamEntity;
import com.seoultech.ecc.team.repository.OneTimeTeamInfoRepository;
import com.seoultech.ecc.team.repository.TeamMemberRepository;
import com.seoultech.ecc.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminTeamService {

    private final TeamRepository teamRepository;
    private final OneTimeTeamInfoRepository oneTimeTeamInfoRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final MemberRepository memberRepository;

    /**
     * 관리자 전용 - 번개 스터디 완전 삭제
     */
    @Transactional
    public void deleteOneTimeTeam(Long teamId, String adminId) {
        // 관리자 권한 확인
        if (!isAdmin(adminId)) {
            throw new IllegalStateException("관리자만 번개 스터디를 삭제할 수 있습니다.");
        }

        // 팀 조회
        TeamEntity team = getOneTimeTeam(teamId);

        // 번개 스터디 정보 먼저 삭제 (외래 키 제약으로 인해)
        oneTimeTeamInfoRepository.delete(team.getOneTimeInfo());

        // 팀 멤버 정보 삭제 (외래 키 제약으로 인해)
        teamMemberRepository.deleteAll(team.getTeamMembers());

        // 팀 정보 삭제
        teamRepository.delete(team);
    }

    /**
     * 팀 엔티티 조회
     */
    private TeamEntity getOneTimeTeam(Long teamId) {
        TeamEntity team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팀입니다. ID: " + teamId));

        if (team.isRegular()) {
            throw new IllegalArgumentException("번개 스터디가 아닙니다. ID: " + teamId);
        }

        if (team.getOneTimeInfo() == null) {
            throw new IllegalStateException("번개 스터디 정보가 없습니다. ID: " + teamId);
        }

        return team;
    }

    /**
     * 관리자 여부 확인
     */
    private boolean isAdmin(String studentId) {
        MemberEntity member = memberRepository.findByStudentId(studentId).orElse(null);
        return member != null && "ROLE_ADMIN".equals(member.getRole());
    }
}