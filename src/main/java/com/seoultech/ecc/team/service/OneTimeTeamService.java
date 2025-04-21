package com.seoultech.ecc.team.service;

import com.seoultech.ecc.member.datamodel.MemberEntity;
import com.seoultech.ecc.member.datamodel.MemberStatus;
import com.seoultech.ecc.member.repository.MemberRepository;
import com.seoultech.ecc.team.datamodel.OneTimeTeamInfoEntity;
import com.seoultech.ecc.team.datamodel.OneTimeTeamStatus;
import com.seoultech.ecc.team.datamodel.SubjectEntity;
import com.seoultech.ecc.team.datamodel.TeamEntity;
import com.seoultech.ecc.team.datamodel.TeamMemberEntity;
import com.seoultech.ecc.team.datamodel.TimeEntity;
import com.seoultech.ecc.team.dto.OneTimeTeamDto;
import com.seoultech.ecc.team.repository.OneTimeTeamInfoRepository;
import com.seoultech.ecc.team.repository.SubjectRepository;
import com.seoultech.ecc.team.repository.TeamMemberRepository;
import com.seoultech.ecc.team.repository.TeamRepository;
import com.seoultech.ecc.team.repository.TimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OneTimeTeamService {

    private final TeamRepository teamRepository;
    private final OneTimeTeamInfoRepository oneTimeTeamInfoRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final MemberRepository memberRepository;
    private final SubjectRepository subjectRepository;
    private final TimeRepository timeRepository;

    // 기본 시간대 설정 (한국 시간)
    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");

    /**
     * 모집 중인 번개 스터디 목록 조회
     */
    @Transactional(readOnly = true)
    public OneTimeTeamDto.ListResponse getOneTimeTeams() {
        List<OneTimeTeamInfoEntity> oneTimeInfos = oneTimeTeamInfoRepository.findByStatusIn(
                List.of(OneTimeTeamStatus.RECRUITING, OneTimeTeamStatus.UPCOMING),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        List<TeamEntity> teams = oneTimeInfos.stream()
                .map(OneTimeTeamInfoEntity::getTeam)
                .collect(Collectors.toList());

        return OneTimeTeamDto.ListResponse.fromEntities(teams);
    }

    /**
     * 전체 번개 스터디 목록 조회
     */
    @Transactional(readOnly = true)
    public OneTimeTeamDto.ListResponse getAllOneTimeTeams() {
        List<TeamEntity> teams = teamRepository.findByIsRegular(false,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return OneTimeTeamDto.ListResponse.fromEntities(teams);
    }

    /**
     * 특정 상태의 번개 스터디 목록 조회
     */
    @Transactional(readOnly = true)
    public OneTimeTeamDto.ListResponse getOneTimeTeamsByStatus(OneTimeTeamStatus status) {
        List<OneTimeTeamInfoEntity> oneTimeInfos = oneTimeTeamInfoRepository.findByStatus(
                status,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        List<TeamEntity> teams = oneTimeInfos.stream()
                .map(OneTimeTeamInfoEntity::getTeam)
                .collect(Collectors.toList());

        return OneTimeTeamDto.ListResponse.fromEntities(teams);
    }

    /**
     * 번개 스터디 상세 조회
     */
    @Transactional(readOnly = true)
    public OneTimeTeamDto.DetailResponse getOneTimeTeamDetail(Long teamId, String currentUserId) {
        TeamEntity team = getOneTimeTeam(teamId);
        return OneTimeTeamDto.DetailResponse.fromEntity(team, currentUserId);
    }

    /**
     * 번개 스터디 생성
     */
    @Transactional
    public OneTimeTeamDto.Response createOneTimeTeam(String studentId, OneTimeTeamDto.CreateRequest request) {
        // 회원 조회 및 상태 확인
        MemberEntity member = getMemberAndCheckStatus(studentId);

        // 현재 시간 (서버 시간대 기준)
        LocalDateTime now = LocalDateTime.now();

        // 시작 시간이 현재보다 이전인지 확인
        if (request.getStartTime().isBefore(now)) {
            throw new IllegalArgumentException("시작 시간은 현재 시간보다 이후여야 합니다.");
        }

        // 종료 시간이 시작 시간보다 이전인지 확인
        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new IllegalArgumentException("종료 시간은 시작 시간보다 이후여야 합니다.");
        }

        // 과목 정보 조회
        SubjectEntity subject = getSubjectById(request.getSubjectId());

        // 기본 시간 정보 사용 (추후 개선 가능)
        TimeEntity defaultTime = timeRepository.findById(1L)
                .orElseThrow(() -> new IllegalStateException("기본 시간 정보가 존재하지 않습니다."));

        // 현재 년도 및 학기 정보 (시스템 정책에 따라 구해야 함)
        int currentYear = LocalDateTime.now().getYear();
        int currentSemester = LocalDateTime.now().getMonthValue() <= 6 ? 1 : 2;

        // 번개 스터디 팀 생성
        TeamEntity team = new TeamEntity();
        team.setName(request.getName());
        team.setSubject(subject);
        team.setTime(defaultTime); // 기본 시간 정보 사용
        team.setScore(0);
        team.setYear(currentYear);
        team.setSemester(currentSemester);
        team.setRegular(false);
        team.setStudyCount(0);

        // 번개 스터디 정보 생성
        OneTimeTeamInfoEntity oneTimeInfo = new OneTimeTeamInfoEntity();
        oneTimeInfo.setTeam(team);
        oneTimeInfo.setStartTime(request.getStartTime());
        oneTimeInfo.setEndTime(request.getEndTime());
        oneTimeInfo.setMaxMembers(request.getMaxMembers());
        oneTimeInfo.setMinMembers(request.getMinMembers());
        oneTimeInfo.setStatus(OneTimeTeamStatus.RECRUITING);
        oneTimeInfo.setDescription(request.getDescription());
        oneTimeInfo.setLocation(request.getLocation());

        team.setOneTimeInfo(oneTimeInfo);

        TeamEntity savedTeam = teamRepository.save(team);

        // 생성자를 팀원으로 자동 추가
        addMemberToTeam(savedTeam, member);

        return OneTimeTeamDto.Response.fromEntity(savedTeam);
    }

    /**
     * 번개 스터디 수정
    */
    @Transactional
    public OneTimeTeamDto.Response updateOneTimeTeam(Long teamId, String studentId, OneTimeTeamDto.UpdateRequest request) {
        // 회원 조회 및 상태 확인
        getMemberAndCheckStatus(studentId);

        // 팀 조회
        TeamEntity team = getOneTimeTeam(teamId);
        OneTimeTeamInfoEntity oneTimeInfo = team.getOneTimeInfo();

        // 권한 확인 (생성자 또는 관리자만 수정 가능)
        checkUpdateDeletePermission(team, studentId);

        // 이미 취소되었거나 완료된 스터디는 수정 불가
        if (oneTimeInfo.getStatus() == OneTimeTeamStatus.CANCELED ||
                oneTimeInfo.getStatus() == OneTimeTeamStatus.COMPLETED) {
            throw new IllegalStateException("취소되었거나 완료된 번개 스터디는 수정할 수 없습니다.");
        }

        // 이미 시작된 스터디는 일부 정보만 수정 가능
        boolean alreadyStarted = oneTimeInfo.getStatus() == OneTimeTeamStatus.IN_PROGRESS;

        // 현재 시간 (서버 시간대 기준)
        LocalDateTime now = LocalDateTime.now();

        // 필드 업데이트
        if (request.getName() != null) {
            team.setName(request.getName());
        }

        // 과목 변경은 스터디가 시작되지 않은 경우에만 가능
        if (request.getSubjectId() != null && !alreadyStarted) {
            team.setSubject(getSubjectById(request.getSubjectId()));
        }

        if (request.getMaxMembers() != null) {
            // 이미 가입된 멤버보다 적게 설정할 수 없음
            if (request.getMaxMembers() < team.getTeamMembers().size()) {
                throw new IllegalArgumentException("최대 인원은 현재 참여 중인 인원보다 적게 설정할 수 없습니다.");
            }
            oneTimeInfo.setMaxMembers(request.getMaxMembers());
        }

        if (request.getMinMembers() != null) {
            // 최소 인원은 현재 참여 인원보다 많게 설정할 수 없음
            if (request.getMinMembers() > team.getTeamMembers().size()) {
                throw new IllegalArgumentException("최소 인원은 현재 참여 중인 인원보다 많게 설정할 수 없습니다.");
            }
            oneTimeInfo.setMinMembers(request.getMinMembers());
        }

        // 시간 변경은 스터디가 시작되지 않은 경우에만 가능
        if (request.getStartTime() != null && !alreadyStarted) {
            // 시작 시간이 현재보다 이전인지 확인
            if (request.getStartTime().isBefore(now)) {
                throw new IllegalArgumentException("시작 시간은 현재 시간보다 이후여야 합니다.");
            }
            oneTimeInfo.setStartTime(request.getStartTime());
        }

        if (request.getEndTime() != null) {
            // 종료 시간은 시작 시간보다 이전일 수 없음
            LocalDateTime startTime = request.getStartTime() != null ?
                    request.getStartTime() : oneTimeInfo.getStartTime();
            if (request.getEndTime().isBefore(startTime)) {
                throw new IllegalArgumentException("종료 시간은 시작 시간보다 이후여야 합니다.");
            }

            // 진행 중인 스터디의 경우 종료 시간은 현재보다 나중이어야 함
            if (alreadyStarted && request.getEndTime().isBefore(now)) {
                throw new IllegalArgumentException("종료 시간은 현재 시간보다 이후여야 합니다.");
            }

            oneTimeInfo.setEndTime(request.getEndTime());
        }

        if (request.getDescription() != null) {
            oneTimeInfo.setDescription(request.getDescription());
        }

        if (request.getLocation() != null) {
            oneTimeInfo.setLocation(request.getLocation());
        }

        // 상태 업데이트
        oneTimeInfo.updateStatus();

        TeamEntity updatedTeam = teamRepository.save(team);

        return OneTimeTeamDto.Response.fromEntity(updatedTeam);
    }

    /**
     * 번개 스터디 신청
     */
    @Transactional
    public OneTimeTeamDto.Response applyToOneTimeTeam(Long teamId, String studentId) {
        // 회원 조회 및 상태 확인
        MemberEntity member = getMemberAndCheckStatus(studentId);

        // 팀 조회
        TeamEntity team = getOneTimeTeam(teamId);
        OneTimeTeamInfoEntity oneTimeInfo = team.getOneTimeInfo();

        // 번개 스터디 신청 가능 상태인지 확인
        if (!oneTimeInfo.isApplicable()) {
            throw new IllegalStateException("현재 신청 가능한 상태가 아닙니다. 모집이 마감되었거나 인원이 꽉 찼습니다.");
        }

        // 이미 신청한 회원인지 확인
        boolean alreadyApplied = team.getTeamMembers().stream()
                .anyMatch(tm -> tm.getMember().getStudentId().equals(studentId));

        if (alreadyApplied) {
            throw new IllegalStateException("이미 신청한 번개 스터디입니다.");
        }

        // 팀원 추가
        addMemberToTeam(team, member);

        // 최소 인원 충족 시 상태 업데이트
        oneTimeInfo.updateStatus();
        teamRepository.save(team);

        return OneTimeTeamDto.Response.fromEntity(team);
    }

    /**
     * 번개 스터디 신청 취소
     */
    @Transactional
    public OneTimeTeamDto.Response cancelOneTimeTeamApplication(Long teamId, String studentId) {
        // 회원 조회 및 상태 확인
        MemberEntity member = getMemberAndCheckStatus(studentId);

        // 팀 조회
        TeamEntity team = getOneTimeTeam(teamId);
        OneTimeTeamInfoEntity oneTimeInfo = team.getOneTimeInfo();

        // 취소 가능 시간인지 확인 (시작 3시간 전까지만 취소 가능)
        if (!oneTimeInfo.isCancelable()) {
            throw new IllegalStateException("취소 가능 시간이 지났습니다. 스터디 시작 3시간 전까지만 취소 가능합니다.");
        }

        // 팀원인지 확인
        Optional<TeamMemberEntity> teamMember = team.getTeamMembers().stream()
                .filter(tm -> tm.getMember().getStudentId().equals(studentId))
                .findFirst();

        if (teamMember.isEmpty()) {
            throw new IllegalStateException("이 번개 스터디에 참여하고 있지 않습니다.");
        }

        // 생성자는 취소할 수 없음 (삭제만 가능)
        if (team.getCreatedBy().equals(studentId)) {
            throw new IllegalStateException("스터디 생성자는 신청 취소 대신 스터디를 삭제해야 합니다.");
        }

        // 신청 취소 (팀원 삭제)
        team.getTeamMembers().remove(teamMember.get());
        teamMemberRepository.delete(teamMember.get());

        // 상태 업데이트
        oneTimeInfo.updateStatus();
        teamRepository.save(team);

        return OneTimeTeamDto.Response.fromEntity(team);
    }

    /**
     * 번개 스터디 삭제 (개설자만 가능)
     */
    @Transactional
    public void deleteOneTimeTeam(Long teamId, String studentId) {
        // 회원 조회 및 상태 확인
        getMemberAndCheckStatus(studentId);

        // 팀 조회
        TeamEntity team = getOneTimeTeam(teamId);
        OneTimeTeamInfoEntity oneTimeInfo = team.getOneTimeInfo();

        // 권한 확인 (생성자 또는 관리자만 삭제 가능)
        checkUpdateDeletePermission(team, studentId);

        // 삭제 대신 취소 상태로 변경
        oneTimeInfo.setStatus(OneTimeTeamStatus.CANCELED);
        teamRepository.save(team);
    }

    /**
     * 번개 스터디 상태 자동 업데이트 스케줄러 (1분마다 실행)
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void updateOneTimeTeamStatuses() {
        List<OneTimeTeamInfoEntity> oneTimeInfos = oneTimeTeamInfoRepository.findByStatusNot(
                OneTimeTeamStatus.COMPLETED
        );

        for (OneTimeTeamInfoEntity oneTimeInfo : oneTimeInfos) {
            oneTimeInfo.updateStatus();
        }

        if (!oneTimeInfos.isEmpty()) {
            oneTimeTeamInfoRepository.saveAll(oneTimeInfos);
        }
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
     * 회원 조회 및 상태 확인 (ACTIVE만 가능)
     */
    private MemberEntity getMemberAndCheckStatus(String studentId) {
        MemberEntity member = memberRepository.findByStudentId(studentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. 학번: " + studentId));

        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new IllegalStateException("ACTIVE 상태의 회원만 번개 스터디를 이용할 수 있습니다.");
        }

        return member;
    }

    /**
     * 과목 ID로 과목 조회
     */
    private SubjectEntity getSubjectById(Long subjectId) {
        return subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 과목입니다. ID: " + subjectId));
    }

    /**
     * 수정/삭제 권한 확인 (생성자 또는 관리자만 가능)
     */
    private void checkUpdateDeletePermission(TeamEntity team, String studentId) {
        if (!team.getCreatedBy().equals(studentId) && !isAdmin(studentId)) {
            throw new IllegalStateException("번개 스터디 생성자 또는 관리자만 수정/삭제할 수 있습니다.");
        }
    }

    /**
     * 관리자 여부 확인
     */
    private boolean isAdmin(String studentId) {
        MemberEntity member = memberRepository.findByStudentId(studentId).orElse(null);
        return member != null && "ROLE_ADMIN".equals(member.getRole());
    }

    /**
     * 팀에 멤버 추가
     */
    private void addMemberToTeam(TeamEntity team, MemberEntity member) {
        TeamMemberEntity teamMember = new TeamMemberEntity();
        teamMember.setTeam(team);
        teamMember.setMember(member);

        teamMemberRepository.save(teamMember);
        team.getTeamMembers().add(teamMember);
    }
}