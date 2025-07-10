package com.seoultech.ecc.member.service;

import com.seoultech.ecc.member.dto.SignupRequest;
import com.seoultech.ecc.member.dto.level.LevelChangeRequestDto;
import com.seoultech.ecc.member.dto.MemberResponse;
import com.seoultech.ecc.member.datamodel.LevelChangeRequestEntity;
import com.seoultech.ecc.member.datamodel.MajorEntity;
import com.seoultech.ecc.member.datamodel.MemberEntity;
import com.seoultech.ecc.member.datamodel.MemberStatus;
import com.seoultech.ecc.member.repository.LevelChangeRequestRepository;
import com.seoultech.ecc.member.repository.MajorRepository;
import com.seoultech.ecc.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MajorRepository majorRepository;
    private final LevelChangeRequestRepository levelChangeRequestRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * UUID로 회원 조회 (존재하지 않으면 예외 발생)
     */
    public MemberEntity getMemberByUuid(Integer uuid) {
        return memberRepository.findById(uuid)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다. UUID: " + uuid));
    }

    /**
     * 학번으로 회원 조회 (존재하지 않으면 예외 발생) - 로그인 ID 찾기용으로만 사용
     */
    private MemberEntity getMemberByStudentId(String studentId) {
        return memberRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다. 학번: " + studentId));
    }

    /**
     * 회원 정보 조회 - 상태에 따라 적절한 정보 반환 (UUID 사용)
     */
    @Transactional(readOnly = true)
    public MemberResponse getMemberInfo(Integer uuid) {
        MemberEntity member = getMemberByUuid(uuid);
        return MemberResponse.fromEntity(member);
    }

    /**
     * 회원 정보 조회 - 상태에 따라 적절한 정보 반환 (학번 사용 - 기존 호환성 유지용)
     */
    @Transactional(readOnly = true)
    public MemberResponse getMemberInfoByStudentId(String studentId) {
        MemberEntity member = getMemberByStudentId(studentId);
        return MemberResponse.fromEntity(member);
    }

    /**
     * PENDING 상태 회원의 가입 신청서 수정 (UUID 사용)
     */
    @Transactional
    public MemberResponse updatePendingApplication(Integer uuid, SignupRequest request) {
        MemberEntity member = getMemberByUuid(uuid);

        if (member.getStatus() != MemberStatus.PENDING) {
            throw new RuntimeException("가입 신청 상태인 회원만 신청서를 수정할 수 있습니다.");
        }

        // 학과 정보 조회
        MajorEntity major = majorRepository.findById(request.getMajorId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 학과입니다. ID: " + request.getMajorId()));

        // 회원 정보 업데이트
        member.setName(request.getName());
        member.setTel(request.getTel());
        member.setKakaoTel(request.getKakaoTel());
        member.setEmail(request.getEmail());
        member.setLevel(request.getLevel());
        member.setMajor(major);
        member.setMotivation(request.getMotivation());
        // 비밀번호 업데이트 (암호화)
        member.setPassword(passwordEncoder.encode(request.getTel()));

        MemberEntity updatedMember = memberRepository.save(member);
        return MemberResponse.fromEntity(updatedMember);
    }

    /**
     * PENDING 상태 회원의 가입 신청 취소 (UUID 사용)
     */
    @Transactional
    public void cancelPendingApplication(Integer uuid) {
        MemberEntity member = getMemberByUuid(uuid);

        if (member.getStatus() != MemberStatus.PENDING) {
            throw new RuntimeException("가입 신청 상태인 회원만 신청을 취소할 수 있습니다.");
        }

        memberRepository.delete(member);
    }

    /**
     * 비밀번호 변경 (UUID 사용)
     */
    @Transactional
    public void updatePassword(Integer uuid, String currentPassword, String newPassword) {
        MemberEntity member = getMemberByUuid(uuid);

        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new RuntimeException("승인된 회원만 비밀번호를 변경할 수 있습니다.");
        }

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 저장
        member.setPassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
    }

    /**
     * 영어 레벨 변경 신청 (UUID 사용)
     */
    @Transactional
    public void requestLevelChange(Integer uuid, Integer requestedLevel, String reason) {
        MemberEntity member = getMemberByUuid(uuid);

        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new RuntimeException("승인된 회원만 레벨 변경을 신청할 수 있습니다.");
        }

        // 진행 중인 레벨 변경 요청이 있는지 확인
        List<LevelChangeRequestEntity> pendingRequests = levelChangeRequestRepository.findByMemberAndStatus(
                member, LevelChangeRequestEntity.RequestStatus.PENDING
        );

        if (!pendingRequests.isEmpty()) {
            throw new RuntimeException("진행 중인 레벨 변경 요청이 있습니다. 기존 요청의 처리 완료 후 다시 시도해주세요.");
        }

        // 현재 레벨과 동일한 레벨로 변경 요청이 들어온 경우
        if (member.getLevel().equals(requestedLevel)) {
            throw new RuntimeException("현재 레벨과 동일한 레벨로 변경할 수 없습니다.");
        }

        // 레벨 변경 요청 생성
        LevelChangeRequestEntity levelChangeRequest = LevelChangeRequestEntity.builder()
                .member(member)
                .currentLevel(member.getLevel())
                .requestedLevel(requestedLevel)
                .status(LevelChangeRequestEntity.RequestStatus.PENDING)
                .reason(reason)
                .build();

        levelChangeRequestRepository.save(levelChangeRequest);
    }

    /**
     * 영어 레벨 변경 신청(간단 버전) (UUID 사용)
     */
    @Transactional
    public void requestLevelChange(Integer uuid, Integer requestedLevel) {
        requestLevelChange(uuid, requestedLevel, null);
    }

    /**
     * 회원의 레벨 변경 요청 목록 조회 (UUID 사용)
     */
    @Transactional(readOnly = true)
    public List<LevelChangeRequestDto> getMemberLevelChangeRequests(Integer uuid) {
        MemberEntity member = getMemberByUuid(uuid);

        List<LevelChangeRequestEntity> requests = levelChangeRequestRepository.findByMember(member);
        return requests.stream()
                .map(LevelChangeRequestDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 회원 탈퇴 (UUID 사용)
     */
    @Transactional
    public void withdrawMembership(Integer uuid) {
        MemberEntity member = getMemberByUuid(uuid);

        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new RuntimeException("승인된 회원만 탈퇴할 수 있습니다.");
        }

        // 상태를 WITHDRAWN으로 변경
        member.setStatus(MemberStatus.WITHDRAWN);
        memberRepository.save(member);
    }
}