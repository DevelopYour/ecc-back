package com.seoultech.ecc.team.security;

import com.seoultech.ecc.member.repository.MemberRepository;
import com.seoultech.ecc.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Spring Security의 SpEL 표현식에서 사용될 팀 멤버십 검사 클래스
 */
@Component
@RequiredArgsConstructor
public class TeamMemberChecker {

    private final TeamService teamService;
    private final MemberRepository memberRepository;

    /**
     * 현재 사용자가 특정 팀의 멤버인지 확인
     * 관리자는 항상 접근 가능하도록 설정
     */
    public boolean isTeamMember(Long teamId, String studentId) {
        // 관리자 체크
        boolean isAdmin = memberRepository.findByStudentId(studentId)
                .map(member -> "ROLE_ADMIN".equals(member.getRole()))
                .orElse(false);

        // 관리자이거나 팀 멤버인 경우 접근 허용
        return isAdmin || teamService.isTeamMember(teamId, studentId);
    }
}