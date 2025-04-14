package com.seoultech.ecc.service;

import com.seoultech.ecc.dto.level.LevelChangeRequestDto;
import com.seoultech.ecc.dto.member.MemberResponse;
import com.seoultech.ecc.entity.LevelChangeRequestEntity;
import com.seoultech.ecc.entity.MemberEntity;
import com.seoultech.ecc.entity.MemberStatus;
import com.seoultech.ecc.repository.LevelChangeRequestRepository;
import com.seoultech.ecc.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MemberRepository memberRepository;
    private final LevelChangeRequestRepository levelChangeRequestRepository;

    /**
     * 학번으로 회원 조회 (존재하지 않으면 예외 발생)
     */
    private MemberEntity getMemberByStudentId(String studentId) {
        return memberRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다. 학번: " + studentId));
    }

    /**
     * 모든 회원 조회
     */
    @Transactional(readOnly = true)
    public List<MemberResponse> getAllMembers() {
        List<MemberEntity> members = memberRepository.findAll();
        return members.stream()
                .map(MemberResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 상태별 회원 조회
     */
    @Transactional(readOnly = true)
    public List<MemberResponse> getMembersByStatus(MemberStatus status) {
        List<MemberEntity> members = memberRepository.findByStatus(status);
        return members.stream()
                .map(MemberResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 영어 레벨별 회원 조회
     */
    @Transactional(readOnly = true)
    public List<MemberResponse> getMembersByLevel(Integer level) {
        List<MemberEntity> members = memberRepository.findByLevel(level);
        return members.stream()
                .map(MemberResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 상태와 레벨로 회원 필터링
     */
    @Transactional(readOnly = true)
    public List<MemberResponse> getMembersByStatusAndLevel(MemberStatus status, Integer level) {
        List<MemberEntity> members = memberRepository.findByStatusAndLevel(status, level);
        return members.stream()
                .map(MemberResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 회원 상태 업데이트
     */
    @Transactional
    public MemberResponse updateMemberStatus(String studentId, MemberStatus status) {
        MemberEntity member = getMemberByStudentId(studentId);
        member.setStatus(status);
        MemberEntity updatedMember = memberRepository.save(member);
        return MemberResponse.fromEntity(updatedMember);
    }

    /**
     * 회원 영어 레벨 업데이트
     */
    @Transactional
    public MemberResponse updateMemberLevel(String studentId, Integer level) {
        MemberEntity member = getMemberByStudentId(studentId);
        member.setLevel(level);
        MemberEntity updatedMember = memberRepository.save(member);
        return MemberResponse.fromEntity(updatedMember);
    }

    /**
     * 보류 중인 레벨 변경 요청 목록 조회
     */
    @Transactional(readOnly = true)
    public List<LevelChangeRequestDto> getPendingLevelChangeRequests() {
        List<LevelChangeRequestEntity> requests = levelChangeRequestRepository.findByStatus(
                LevelChangeRequestEntity.RequestStatus.PENDING
        );

        return requests.stream()
                .map(LevelChangeRequestDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 레벨 변경 요청 승인
     */
    @Transactional
    public MemberResponse approveLevelChangeRequest(Long requestId) {
        LevelChangeRequestEntity request = levelChangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 레벨 변경 요청입니다. ID: " + requestId));

        if (request.getStatus() != LevelChangeRequestEntity.RequestStatus.PENDING) {
            throw new RuntimeException("승인 대기 상태의 요청만 승인할 수 있습니다.");
        }

        MemberEntity member = request.getMember();
        member.setLevel(request.getRequestedLevel());

        request.setStatus(LevelChangeRequestEntity.RequestStatus.APPROVED);

        memberRepository.save(member);
        levelChangeRequestRepository.save(request);

        return MemberResponse.fromEntity(member);
    }

    /**
     * 레벨 변경 요청 거절
     */
    @Transactional
    public void rejectLevelChangeRequest(Long requestId) {
        LevelChangeRequestEntity request = levelChangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 레벨 변경 요청입니다. ID: " + requestId));

        if (request.getStatus() != LevelChangeRequestEntity.RequestStatus.PENDING) {
            throw new RuntimeException("승인 대기 상태의 요청만 거절할 수 있습니다.");
        }

        request.setStatus(LevelChangeRequestEntity.RequestStatus.REJECTED);
        levelChangeRequestRepository.save(request);
    }

    /**
     * 회원 가입 승인
     */
    @Transactional
    public MemberResponse approveApplication(String studentId) {
        MemberEntity member = getMemberByStudentId(studentId);

        if (member.getStatus() != MemberStatus.PENDING) {
            throw new RuntimeException("승인 대기 상태의 회원만 승인할 수 있습니다.");
        }

        member.setStatus(MemberStatus.ACTIVE);
        MemberEntity updatedMember = memberRepository.save(member);

        return MemberResponse.fromEntity(updatedMember);
    }

    /**
     * 회원 가입 거절
     */
    @Transactional
    public void rejectApplication(String studentId) {
        MemberEntity member = getMemberByStudentId(studentId);

        if (member.getStatus() != MemberStatus.PENDING) {
            throw new RuntimeException("승인 대기 상태의 회원만 거절할 수 있습니다.");
        }

        memberRepository.delete(member);
    }
}