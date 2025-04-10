package com.seoultech.ecc.service;

import com.seoultech.ecc.dto.auth.SignupRequest;
import com.seoultech.ecc.dto.member.MemberResponse;
import com.seoultech.ecc.entity.MajorEntity;
import com.seoultech.ecc.entity.MemberEntity;
import com.seoultech.ecc.entity.MemberStatus;
import com.seoultech.ecc.repository.MajorRepository;
import com.seoultech.ecc.repository.MemberRepository;
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
    private final BCryptPasswordEncoder passwordEncoder;

    // PENDING 상태 회원용 메서드들

    /**
     * PENDING 상태 회원의 가입 신청서 조회
     */
    @Transactional(readOnly = true)
    public MemberResponse getPendingApplication(String studentId) {
        MemberEntity member = memberRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        if (member.getStatus() != MemberStatus.PENDING) {
            throw new RuntimeException("가입 신청 상태인 회원만 조회할 수 있습니다.");
        }

        return MemberResponse.fromEntity(member);
    }

    /**
     * PENDING 상태 회원의 가입 신청서 수정
     */
    @Transactional
    public MemberResponse updatePendingApplication(String studentId, SignupRequest request) {
        MemberEntity member = memberRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        if (member.getStatus() != MemberStatus.PENDING) {
            throw new RuntimeException("가입 신청 상태인 회원만 신청서를 수정할 수 있습니다.");
        }

        // 학과 정보 조회
        MajorEntity major = majorRepository.findById(request.getMajorId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 학과입니다."));

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
     * PENDING 상태 회원의 가입 신청 취소
     */
    @Transactional
    public void cancelPendingApplication(String studentId) {
        MemberEntity member = memberRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        if (member.getStatus() != MemberStatus.PENDING) {
            throw new RuntimeException("가입 신청 상태인 회원만 신청을 취소할 수 있습니다.");
        }

        memberRepository.delete(member);
    }

    // ACTIVE 상태 회원용 메서드들

    /**
     * ACTIVE 상태 회원 정보 조회
     */
    @Transactional(readOnly = true)
    public MemberResponse getActiveMemberInfo(String studentId) {
        MemberEntity member = memberRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new RuntimeException("승인된 회원만 정보를 조회할 수 있습니다.");
        }

        return MemberResponse.fromEntity(member);
    }

    /**
     * 비밀번호 변경
     */
    @Transactional
    public void updatePassword(String studentId, String currentPassword, String newPassword) {
        MemberEntity member = memberRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

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
     * 영어 레벨 변경 신청
     */
    @Transactional
    public void requestLevelChange(String studentId, Integer level) {
        MemberEntity member = memberRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new RuntimeException("승인된 회원만 레벨 변경을 신청할 수 있습니다.");
        }

        // 변경 신청만 하고 실제 변경은 관리자가 승인해야 함
        // 여기서는 관리자 승인 없이 바로 변경으로 구현
        member.setLevel(level);
        memberRepository.save(member);
    }

    /**
     * 회원 탈퇴
     */
    @Transactional
    public void withdrawMembership(String studentId) {
        MemberEntity member = memberRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new RuntimeException("승인된 회원만 탈퇴할 수 있습니다.");
        }

        // 상태를 WITHDRAWN으로 변경
        member.setStatus(MemberStatus.WITHDRAWN);
        memberRepository.save(member);
    }

    /**
     * 관리자를 위한 모든 회원 조회 메서드
     */
    @Transactional(readOnly = true)
    public List<MemberResponse> getAllMembers() {
        List<MemberEntity> members = memberRepository.findAll();
        return members.stream()
                .map(MemberResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 관리자를 위한 특정 상태의 회원 조회 메서드
     */
    @Transactional(readOnly = true)
    public List<MemberResponse> getMembersByStatus(MemberStatus status) {
        List<MemberEntity> members = memberRepository.findByStatus(status);
        return members.stream()
                .map(MemberResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 관리자를 위한 회원 상태 업데이트 메서드
     */
    @Transactional
    public MemberResponse updateMemberStatus(String studentId, MemberStatus status) {
        MemberEntity member = memberRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        member.setStatus(status);
        MemberEntity updatedMember = memberRepository.save(member);

        return MemberResponse.fromEntity(updatedMember);
    }

    /**
     * 관리자를 위한 회원 가입 승인 메서드
     */
    @Transactional
    public MemberResponse approveApplication(String studentId) {
        MemberEntity member = memberRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        if (member.getStatus() != MemberStatus.PENDING) {
            throw new RuntimeException("승인 대기 상태의 회원만 승인할 수 있습니다.");
        }

        member.setStatus(MemberStatus.ACTIVE);
        MemberEntity updatedMember = memberRepository.save(member);

        return MemberResponse.fromEntity(updatedMember);
    }

    /**
     * 관리자를 위한 회원 가입 거절 메서드
     */
    @Transactional
    public void rejectApplication(String studentId) {
        MemberEntity member = memberRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        if (member.getStatus() != MemberStatus.PENDING) {
            throw new RuntimeException("승인 대기 상태의 회원만 거절할 수 있습니다.");
        }

        memberRepository.delete(member);
    }
}