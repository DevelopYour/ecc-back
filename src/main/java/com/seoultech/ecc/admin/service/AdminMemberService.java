package com.seoultech.ecc.admin.service;

import com.seoultech.ecc.member.dto.MemberSimpleDto;
import com.seoultech.ecc.member.dto.level.LevelChangeRequestDto;
import com.seoultech.ecc.member.dto.MemberResponse;
import com.seoultech.ecc.member.datamodel.LevelChangeRequestEntity;
import com.seoultech.ecc.member.datamodel.MemberEntity;
import com.seoultech.ecc.member.datamodel.MemberStatus;
import com.seoultech.ecc.member.repository.LevelChangeRequestRepository;
import com.seoultech.ecc.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminMemberService {

    private final MemberRepository memberRepository;
    private final LevelChangeRequestRepository levelChangeRequestRepository;

    /**
     * UUID로 회원 조회 (존재하지 않으면 예외 발생)
     */
    private MemberEntity getMemberByUuid(Integer uuid) {
        return memberRepository.findById(uuid)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다. UUID: " + uuid));
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

    public List<MemberSimpleDto>  getAllMemberSimpleDto() {
        return memberRepository.findAll().stream().map(MemberSimpleDto::fromEntity).toList();
    }

    /**
     * 특정 회원 상세 정보 조회 (UUID 사용)
     */
    @Transactional(readOnly = true)
    public MemberResponse getMemberDetail(Integer uuid) {
        MemberEntity member = getMemberByUuid(uuid);
        return MemberResponse.fromEntity(member);
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
     * 회원 상태 업데이트 (UUID 사용)
     */
    @Transactional
    public MemberResponse updateMemberStatus(Integer uuid, MemberStatus status) {
        MemberEntity member = getMemberByUuid(uuid);
        member.setStatus(status);
        MemberEntity updatedMember = memberRepository.save(member);
        return MemberResponse.fromEntity(updatedMember);
    }

    /**
     * 회원 영어 레벨 업데이트 (UUID 사용)
     */
    @Transactional
    public MemberResponse updateMemberLevel(Integer uuid, Integer level) {
        MemberEntity member = getMemberByUuid(uuid);
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
    public MemberResponse approveLevelChangeRequest(Integer requestId) {
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
     * 거절 시 회원의 레벨을 변경하지 않고 요청 상태만 REJECTED로 변경
     */
    @Transactional
    public void rejectLevelChangeRequest(Integer requestId) {
        LevelChangeRequestEntity request = levelChangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 레벨 변경 요청입니다. ID: " + requestId));

        if (request.getStatus() != LevelChangeRequestEntity.RequestStatus.PENDING) {
            throw new RuntimeException("승인 대기 상태의 요청만 거절할 수 있습니다.");
        }

        // 회원의 레벨은 변경하지 않고 요청 상태만 REJECTED로 변경
        request.setStatus(LevelChangeRequestEntity.RequestStatus.REJECTED);
        levelChangeRequestRepository.save(request);
    }

    /**
     * 회원 가입 승인 (UUID 사용)
     */
    @Transactional
    public MemberResponse approveApplication(Integer uuid) {
        MemberEntity member = getMemberByUuid(uuid);

        if (member.getStatus() != MemberStatus.PENDING) {
            throw new RuntimeException("승인 대기 상태의 회원만 승인할 수 있습니다.");
        }

        member.setStatus(MemberStatus.ACTIVE);
        MemberEntity updatedMember = memberRepository.save(member);

        return MemberResponse.fromEntity(updatedMember);
    }

    /**
     * 회원 가입 거절 (UUID 사용)
     */
    @Transactional
    public void rejectApplication(Integer uuid) {
        MemberEntity member = getMemberByUuid(uuid);

        if (member.getStatus() != MemberStatus.PENDING) {
            throw new RuntimeException("승인 대기 상태의 회원만 거절할 수 있습니다.");
        }

        memberRepository.delete(member);
    }
}