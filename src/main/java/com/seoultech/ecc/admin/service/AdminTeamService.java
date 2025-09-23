package com.seoultech.ecc.admin.service;

import com.seoultech.ecc.admin.dto.TeamDetailDto;
import com.seoultech.ecc.admin.dto.TeamMemberOperationResultDto;
import com.seoultech.ecc.admin.dto.TeamMembersDto;
import com.seoultech.ecc.admin.dto.TeamWeekDetailDto;
import com.seoultech.ecc.member.datamodel.MemberEntity;
import com.seoultech.ecc.member.datamodel.MemberStatus;
import com.seoultech.ecc.member.dto.MemberSimpleDto;
import com.seoultech.ecc.member.repository.MemberRepository;
import com.seoultech.ecc.report.datamodel.ReportDocument;
import com.seoultech.ecc.report.repository.ReportRepository;
import com.seoultech.ecc.report.service.ReportService;
import com.seoultech.ecc.review.datamodel.ReviewDocument;
import com.seoultech.ecc.review.datamodel.ReviewStatus;
import com.seoultech.ecc.review.dto.ReviewSummaryDto;
import com.seoultech.ecc.review.repository.ReviewRepository;
import com.seoultech.ecc.review.service.ReviewService;
import com.seoultech.ecc.team.datamodel.TeamEntity;
import com.seoultech.ecc.team.datamodel.TeamMemberEntity;
import com.seoultech.ecc.team.dto.TeamDto;
import com.seoultech.ecc.team.repository.OneTimeTeamInfoRepository;
import com.seoultech.ecc.team.repository.TeamMemberRepository;
import com.seoultech.ecc.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminTeamService {

    private final TeamRepository teamRepository;
    private final OneTimeTeamInfoRepository oneTimeTeamInfoRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final MemberRepository memberRepository;
    private final ReportRepository reportRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewService reviewService;
    private final ReportService reportService;

    /**
     * 전체 팀 목록 조회 (필터링 옵션 포함) (UUID 사용)
     */
    @Transactional(readOnly = true)
    public List<TeamDto> getAllTeams(Integer adminUuid, Boolean regular, Integer semesterId) {
        // 관리자 권한 확인
        checkAdminPermission(adminUuid);

        // 필터 조합에 따른 조회 로직
        List<TeamEntity> teams;

        if (semesterId != null) {
            // 연도와 학기로 필터링
            teams = teamRepository.findBySemester_Id(semesterId);

            // 정규 스터디 여부로 추가 필터링
            if (regular != null) {
                teams = teams.stream()
                        .filter(team -> team.isRegular() == regular)
                        .toList();
            }
        } else if (regular != null) {
            // 정규 스터디 여부로만 필터링
            teams = teamRepository.findByRegular(regular,
                    Sort.by(Sort.Direction.DESC, "createdAt"));
        } else {
            // 필터 없이 전체 조회
            teams = teamRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        }

        // TeamDto 리스트로 변환하여 반환
        return teams.stream()
                .map(TeamDto::fromEntity)
                .toList();
    }

    /**
     * 특정 팀 상세 정보 조회 (UUID 사용)
     */
    @Transactional(readOnly = true)
    public TeamDetailDto getTeamDetail(Integer teamId, Integer adminUuid) {
        // 관리자 권한 확인
        checkAdminPermission(adminUuid);

        // 팀
        TeamDto team = TeamDto.fromEntityWithDetails(getTeamById(teamId), adminUuid);

        // 보고서 요약 목록
        List<ReportDocument> reports = reportRepository.findByTeamIdOrderByWeekAsc(teamId);

        return TeamDetailDto.builder()
                .team(team)
                .reports(reports)
                .build();
    }

    /**
     * 특정 팀의 주차별 상세 정보 조회 (정규 스터디 전용) (UUID 사용)
     */
    @Transactional(readOnly = true)
    public TeamWeekDetailDto getTeamWeekDetail(Integer teamId, String reportId, Integer adminUuid) {
        // 관리자 권한 확인
        checkAdminPermission(adminUuid);

        // 팀 조회
        TeamEntity team = getTeamById(teamId);

        // 번개 스터디 체크
        checkRegularStudy(team);

        // 해당 주차 보고서 조회
        ReportDocument report = reportService.findByReportId(reportId);

        // 주차별 복습 상태 정보 조회
        List<ReviewSummaryDto> reviews = reviewService.getReviewStatusInfos(report.getId());

        // TeamWeekDetailDto로 구성하여 반환
        return TeamWeekDetailDto.builder()
                .team(TeamDto.fromEntity(team))
                .report(report)
                .reviews(reviews)
                .build();
    }

    /**
     * 특정 팀의 주차별 보고서 조회 (정규 스터디 전용) (UUID 사용)
     */
    @Transactional(readOnly = true)
    public ReportDocument getTeamWeekReport(Integer teamId, int week, Integer adminUuid) {
        // 관리자 권한 확인
        checkAdminPermission(adminUuid);

        // 팀 조회
        TeamEntity team = getTeamById(teamId);

        // 번개 스터디 체크
        checkRegularStudy(team);

        // 주차별 보고서 조회
        List<ReportDocument> reports = reportRepository.findByTeamIdOrderByWeekAsc(teamId);

        // 요청한 주차의 보고서 찾기
        return reports.stream()
                .filter(report -> report.getWeek() == week)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("해당 주차의 보고서가 존재하지 않습니다."));
    }

    /**
     * 번개 스터디 보고서 조회 (번개 스터디는 주차 개념 없이 단일 보고서만 존재) (UUID 사용)
     */
    @Transactional(readOnly = true)
    public ReportDocument getOneTimeTeamReport(Integer teamId, Integer adminUuid) {
        // 관리자 권한 확인
        checkAdminPermission(adminUuid);

        // 팀 조회
        TeamEntity team = getTeamById(teamId);

        // 정규 스터디 체크 (번개 스터디만 가능)
        if (team.isRegular()) {
            throw new IllegalArgumentException("번개 스터디가 아닙니다. 정규 스터디는 주차별 보고서 조회를 이용해주세요.");
        }

        // 번개 스터디 보고서 조회 (항상 첫 번째 보고서만 사용)
        List<ReportDocument> reports = reportRepository.findByTeamIdOrderByWeekAsc(teamId);

        if (reports.isEmpty()) {
            throw new RuntimeException("해당 번개 스터디의 보고서가 존재하지 않습니다.");
        }

        return reports.get(0);
    }

    /**
     * 보고서 평가 점수 수정 (정규 스터디 전용) (UUID 사용)
     */
    @Transactional
    public ReportDocument updateReportGrade(Integer teamId, int week, int grade, Integer adminUuid) {
        // 관리자 권한 확인
        checkAdminPermission(adminUuid);

        // 점수 유효성 검사
        if (grade < 0 || grade > 100) {
            throw new IllegalArgumentException("평가 점수는 0-100 사이의 값이어야 합니다.");
        }

        // 팀 조회
        TeamEntity team = getTeamById(teamId);

        // 번개 스터디 체크
        checkRegularStudy(team);

        // 주차별 보고서 조회
        ReportDocument report = getTeamWeekReport(teamId, week, adminUuid);

        // 점수 업데이트
        report.setGrade(grade);

        // 저장
        return reportRepository.save(report);
    }

    /**
     * 관리자 전용 - 번개 스터디 완전 삭제 (UUID 사용)
     */
    @Transactional
    public void deleteOneTimeTeam(Integer teamId, Integer adminUuid) {
        // 관리자 권한 확인
        checkAdminPermission(adminUuid);

        // 팀 조회
        TeamEntity team = getTeamById(teamId);

        if (team.isRegular()) {
            throw new IllegalArgumentException("정규 스터디는 이 메서드로 삭제할 수 없습니다. ID: " + teamId);
        }

        if (team.getOneTimeInfo() == null) {
            throw new IllegalStateException("번개 스터디 정보가 없습니다. ID: " + teamId);
        }

        // 번개 스터디 정보 먼저 삭제 (외래 키 제약으로 인해)
        oneTimeTeamInfoRepository.delete(team.getOneTimeInfo());

        // 팀 멤버 정보 삭제 (외래 키 제약으로 인해)
        teamMemberRepository.deleteAll(team.getTeamMembers());

        // 팀 정보 삭제
        teamRepository.delete(team);
    }

    /**
     * 팀 점수 수동 조정 (정규 스터디 전용) (UUID 사용)
     */
    @Transactional
    public TeamDto updateTeamScore(Integer teamId, int score, Integer adminUuid) {
        // 관리자 권한 확인
        checkAdminPermission(adminUuid);

        // 점수 유효성 검사
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("팀 점수는 0-100 사이의 값이어야 합니다.");
        }

        // 팀 조회
        TeamEntity team = getTeamById(teamId);

        // 번개 스터디 체크
        checkRegularStudy(team);

        // 점수 업데이트
        team.setScore(score);

        // 저장
        TeamEntity updatedTeam = teamRepository.save(team);

        return TeamDto.fromEntityWithDetails(updatedTeam, adminUuid);
    }

    /**
     * 팀 멤버 조회 (UUID 사용)
     */
    @Transactional(readOnly = true)
    public TeamMembersDto getTeamMembers(Integer teamId, Integer adminUuid) {
        // 관리자 권한 확인
        checkAdminPermission(adminUuid);

        // 팀 조회
        TeamEntity team = getTeamById(teamId);

        // 팀 멤버 조회 및 반환
        List<MemberSimpleDto> members = team.getTeamMembers().stream()
                .map(tm -> new MemberSimpleDto(tm.getMember().getId(), tm.getMember().getStudentId(),tm.getMember().getName()))
                .toList();

        // TeamMembersDto로 구성하여 반환
        return TeamMembersDto.builder()
                .teamId(teamId)
                .teamName(team.getName())
                .isRegular(team.isRegular())
                .members(members)
                .build();
    }

    /**
     * 팀에 멤버 추가 (UUID 사용)
     */
    @Transactional
    public TeamMemberOperationResultDto addTeamMember(Integer teamId, Integer memberUuid, Integer adminUuid) {
        // 관리자 권한 확인
        checkAdminPermission(adminUuid);

        // 팀 조회
        TeamEntity team = getTeamById(teamId);

        // 추가할 회원 조회 (UUID 사용)
        MemberEntity member = memberRepository.findById(memberUuid)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다. UUID: " + memberUuid));

        // 이미 팀에 속해 있는지 확인
        boolean alreadyInTeam = team.getTeamMembers().stream()
                .anyMatch(tm -> tm.getMember().getId().equals(memberUuid));

        if (alreadyInTeam) {
            throw new IllegalStateException("이미 팀에 속해 있는 회원입니다.");
        }

        // 회원 ACTIVE 상태 확인
        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new IllegalStateException("ACTIVE 상태의 회원만 팀에 추가할 수 있습니다.");
        }

        // 팀 멤버 추가
        TeamMemberEntity teamMember = new TeamMemberEntity();
        teamMember.setTeam(team);
        teamMember.setMember(member);

        teamMemberRepository.save(teamMember);

        // 결과 반환을 위해 다시 조회
        TeamEntity updatedTeam = getTeamById(teamId);
        List<MemberSimpleDto> updatedMembers = updatedTeam.getTeamMembers().stream()
                .map(tm -> new MemberSimpleDto(tm.getMember().getId(), tm.getMember().getStudentId(), tm.getMember().getName()))
                .toList();

        // TeamMemberOperationResultDto로 구성하여 반환
        return TeamMemberOperationResultDto.builder()
                .teamId(teamId)
                .teamName(updatedTeam.getName())
                .isRegular(updatedTeam.isRegular())
                .members(updatedMembers)
                .build();
    }

    /**
     * 팀에서 멤버 삭제 (UUID 사용)
     */
    @Transactional
    public TeamMemberOperationResultDto removeTeamMember(Integer teamId, Integer memberUuid, Integer adminUuid) {
        // 관리자 권한 확인
        checkAdminPermission(adminUuid);

        // 팀 조회
        TeamEntity team = getTeamById(teamId);

        // 팀에서 해당 멤버 찾기
        Optional<TeamMemberEntity> teamMemberOpt = team.getTeamMembers().stream()
                .filter(tm -> tm.getMember().getId().equals(memberUuid))
                .findFirst();

        if (teamMemberOpt.isEmpty()) {
            throw new RuntimeException("해당 회원은 팀에 속해 있지 않습니다.");
        }

        // 팀 멤버 삭제
        TeamMemberEntity teamMember = teamMemberOpt.get();
        team.getTeamMembers().remove(teamMember);
        teamMemberRepository.delete(teamMember);

        // 결과 반환을 위해 다시 조회
        TeamEntity updatedTeam = getTeamById(teamId);
        List<MemberSimpleDto> updatedMembers = updatedTeam.getTeamMembers().stream()
                .map(tm -> new MemberSimpleDto(tm.getMember().getId(), tm.getMember().getStudentId(), tm.getMember().getName()))
                .toList();

        // TeamMemberOperationResultDto로 구성하여 반환
        return TeamMemberOperationResultDto.builder()
                .teamId(teamId)
                .teamName(updatedTeam.getName())
                .isRegular(updatedTeam.isRegular())
                .members(updatedMembers)
                .build();
    }

    /**
     * 팀 출석/참여율 통계 (정규 스터디 전용) (UUID 사용)
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getTeamAttendanceStats(Integer teamId, Integer adminUuid) {
        // 관리자 권한 확인
        checkAdminPermission(adminUuid);

        // 팀 조회
        TeamEntity team = getTeamById(teamId);

        // 번개 스터디 체크
        checkRegularStudy(team);

        // 팀 보고서 조회
        List<ReportDocument> reports = reportRepository.findByTeamIdOrderByWeekAsc(teamId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("teamId", teamId);
        stats.put("teamName", team.getName());
        stats.put("totalWeeks", reports.size());

        // 팀원별 복습 완료율 계산
        Map<Integer, Map<String, Object>> memberStats = new HashMap<>();

        List<MemberSimpleDto> members = !reports.isEmpty() ? reports.get(0).getMembers() : new ArrayList<>();
        for (MemberSimpleDto member : members) {
            Map<String, Object> memberStat = new HashMap<>();
            memberStat.put("memberId", member.getId());
            memberStat.put("memberName", member.getName());

            // 각 회원의 복습 완료 수
            long completedReviews = 0;
            long totalReviews = 0;

            for (ReportDocument report : reports) {
                if (!report.isSubmitted()) continue;

                totalReviews++;

                // 회원의 복습 상태 조회
                List<ReviewDocument> reviews = reviewRepository.findAllByReportId(report.getId());
                Optional<ReviewDocument> memberReview = reviews.stream()
                        .filter(r -> r.getMember() != null && r.getMember().getId().equals(member.getId()))
                        .findFirst();

                if (memberReview.isPresent() && memberReview.get().getStatus() == ReviewStatus.COMPLETED) {
                    completedReviews++;
                }
            }

            double attendanceRate = totalReviews > 0 ? (double) completedReviews / totalReviews * 100 : 0;
            memberStat.put("attendanceRate", Math.round(attendanceRate * 10) / 10.0); // 소수점 한 자리까지
            memberStat.put("completedReviews", completedReviews);
            memberStat.put("totalReviews", totalReviews);

            memberStats.put(member.getId(), memberStat);
        }

        stats.put("memberStats", memberStats.values());

        // 팀 전체 통계
        long submittedReports = reports.stream().filter(ReportDocument::isSubmitted).count();
        double teamSubmissionRate = !reports.isEmpty() ? (double) submittedReports / reports.size() * 100 : 0;
        stats.put("submittedReports", submittedReports);
        stats.put("teamSubmissionRate", Math.round(teamSubmissionRate * 10) / 10.0);

        // 팀 평균 점수
        OptionalDouble avgGrade = reports.stream()
                .filter(ReportDocument::isSubmitted)
                .mapToInt(ReportDocument::getGrade)
                .average();
        stats.put("averageGrade", avgGrade.isPresent() ? Math.round(avgGrade.getAsDouble() * 10) / 10.0 : 0);

        return stats;
    }

    /**
     * 팀 보고서 제출/평가 현황 조회 (정규 스터디 전용) (UUID 사용)
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getTeamReportsStatus(Integer semesterId, Integer adminUuid) {
        // 관리자 권한 확인
        checkAdminPermission(adminUuid);

        // 필터에 맞는 팀 조회 (정규 스터디만)
        List<TeamEntity> teams = teamRepository.findBySemester_Id(semesterId)
                .stream()
                .filter(TeamEntity::isRegular)
                .toList();

        // 결과 저장 맵
        Map<String, Object> result = new HashMap<>();
//        TODO: 아래 주석된 두 줄 빼거나 semesterId 로 대체하면 될 거 같은데 (효선)
//        result.put("year", currentYear);
//        result.put("semester", currentSemester);

        // 팀별 보고서 현황
        List<Map<String, Object>> teamReportStatusList = new ArrayList<>();

        for (TeamEntity team : teams) {
            Map<String, Object> teamStatus = new HashMap<>();
            teamStatus.put("teamId", team.getId());
            teamStatus.put("teamName", team.getName());

            // 팀의 보고서 조회
            List<ReportDocument> reports = reportRepository.findByTeamIdOrderByWeekAsc(team.getId());

            // 주차별 현황
            List<Map<String, Object>> weeklyStatus = new ArrayList<>();

            for (ReportDocument report : reports) {
                Map<String, Object> weekStatus = new HashMap<>();
                weekStatus.put("week", report.getWeek());
                weekStatus.put("submitted", report.isSubmitted());
                weekStatus.put("grade", report.getGrade());

                // 멤버별 복습 상태
                List<Map<String, Object>> memberReviewStatus = new ArrayList<>();

                if (report.isSubmitted()) {
                    List<ReviewDocument> reviews = reviewRepository.findAllByReportId(report.getId());

                    // 안전하게 멤버 리스트 가져오기
                    List<MemberSimpleDto> members = report.getMembers();
                    for (MemberSimpleDto member : members) {
                        Map<String, Object> memberStatus = new HashMap<>();
                        memberStatus.put("memberId", member.getId());
                        memberStatus.put("memberName", member.getName());

                        Optional<ReviewDocument> reviewOpt = reviews.stream()
                                .filter(r -> r.getMember() != null && r.getMember().getId().equals(member.getId()))
                                .findFirst();

                        ReviewStatus reviewStatus = reviewOpt.isPresent() ? reviewOpt.get().getStatus() : ReviewStatus.NOT_READY;
                        memberStatus.put("reviewStatus", reviewStatus);

                        memberReviewStatus.add(memberStatus);
                    }
                }

                weekStatus.put("memberReviews", memberReviewStatus);
                weeklyStatus.add(weekStatus);
            }

            teamStatus.put("weeklyStatus", weeklyStatus);

            // 요약 통계
            long submittedCount = reports.stream().filter(ReportDocument::isSubmitted).count();
            double submissionRate = !reports.isEmpty() ? (double) submittedCount / reports.size() * 100 : 0;
            OptionalDouble avgGrade = reports.stream()
                    .filter(ReportDocument::isSubmitted)
                    .mapToInt(ReportDocument::getGrade)
                    .average();

            teamStatus.put("totalWeeks", reports.size());
            teamStatus.put("submittedReports", submittedCount);
            teamStatus.put("submissionRate", Math.round(submissionRate * 10) / 10.0);
            teamStatus.put("averageGrade", avgGrade.isPresent() ? Math.round(avgGrade.getAsDouble() * 10) / 10.0 : 0);

            teamReportStatusList.add(teamStatus);
        }

        result.put("teamReportStatus", teamReportStatusList);

        // 전체 통계 요약
        int totalTeams = teams.size();
        long totalSubmittedReports = teamReportStatusList.stream()
                .mapToLong(status -> (Long) status.get("submittedReports"))
                .sum();
        long totalExpectedReports = teamReportStatusList.stream()
                .mapToLong(status -> (Integer) status.get("totalWeeks"))
                .sum();
        double overallSubmissionRate = totalExpectedReports > 0 ?
                (double) totalSubmittedReports / totalExpectedReports * 100 : 0;
        OptionalDouble overallAvgGrade = teamReportStatusList.stream()
                .filter(status -> status.get("averageGrade") != null && (Double) status.get("averageGrade") > 0)
                .mapToDouble(status -> (Double) status.get("averageGrade"))
                .average();

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalTeams", totalTeams);
        summary.put("totalSubmittedReports", totalSubmittedReports);
        summary.put("totalExpectedReports", totalExpectedReports);
        summary.put("overallSubmissionRate", Math.round(overallSubmissionRate * 10) / 10.0);
        summary.put("overallAverageGrade", overallAvgGrade.isPresent() ?
                Math.round(overallAvgGrade.getAsDouble() * 10) / 10.0 : 0);

        result.put("summary", summary);

        return result;
    }

    /**
     * 팀 ID로 팀 조회
     */
    private TeamEntity getTeamById(Integer teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 팀입니다. ID: " + teamId));
    }

    /**
     * 정규 스터디인지 확인 (번개 스터디인 경우 예외 발생)
     */
    private void checkRegularStudy(TeamEntity team) {
        if (!team.isRegular()) {
            throw new IllegalArgumentException("이 기능은 정규 스터디에만 제공됩니다. 번개 스터디에는 적용할 수 없습니다.");
        }
    }

    /**
     * 관리자 권한 확인 (UUID 사용)
     */
    private void checkAdminPermission(Integer uuid) {
        MemberEntity admin = memberRepository.findById(uuid)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 관리자입니다."));

        if (!"ROLE_ADMIN".equals(admin.getRole())) {
            throw new RuntimeException("관리자 권한이 없습니다.");
        }
    }

    public Long countTeams(boolean isRegular) {
        return teamRepository.countByRegular(isRegular);
    }

    public Long countUncheckedReports(){
        return reportRepository.countBySubmittedTrueAndGradeIsNull();
    }
}