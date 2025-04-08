package com.seoultech.ecc.service;

import com.seoultech.ecc.dto.auth.SignupRequest;
import com.seoultech.ecc.dto.member.MemberResponse;
import com.seoultech.ecc.entity.MajorEntity;
import com.seoultech.ecc.entity.MemberEntity;
import com.seoultech.ecc.entity.MemberStatus;
import com.seoultech.ecc.repository.MajorRepository;
import com.seoultech.ecc.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MajorRepository majorRepository;
    private static final String SESSION_USER_KEY = "LOGIN_USER_ID";

    /**
     * 회원 정보 조회
     */
    @Transactional(readOnly = true)
    public MemberResponse getMemberInfo(Integer uuid) {
        MemberEntity member = memberRepository.findById(uuid)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));
        return MemberResponse.fromEntity(member);
    }

    /**
     * 가입 신청서 수정 (PENDING 상태일 때만 가능)
     */
    @Transactional
    public MemberResponse updateApplication(Integer uuid, SignupRequest request) {
        // 회원 조회
        MemberEntity member = memberRepository.findById(uuid)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        // 가입 대기 상태인지 확인
        if (member.getStatus() != MemberStatus.PENDING) {
            throw new RuntimeException("가입 신청 상태에서만 지원서를 수정할 수 있습니다.");
        }

        // 학과 정보 조회
        MajorEntity major = majorRepository.findById(request.getMajorId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 학과입니다."));

        // 영어 실력 레벨 변환 (초급=1, 중급=2, 고급=3)
        int level = 1; // 기본값 초급
        if ("중급".equals(request.getLevel())) {
            level = 2;
        } else if ("고급".equals(request.getLevel())) {
            level = 3;
        }

        // 회원 정보 업데이트
        member.setName(request.getName());
        member.setStudentId(request.getStudentId());
        member.setTel(request.getTel());
        member.setEmail(request.getEmail());
        member.setLevel(level);
        member.setMajor(major);
        member.setMotivation(request.getMotivation());
        // 초기 비밀번호도 전화번호로 업데이트
        member.setPassword(request.getTel());

        // 회원 저장
        MemberEntity updatedMember = memberRepository.save(member);

        // 응답 DTO 변환 및 반환
        return MemberResponse.fromEntity(updatedMember);
    }

    /**
     * 가입 신청 취소 (PENDING 상태일 때만 가능)
     */
    @Transactional
    public void cancelApplication(Integer uuid) {
        // 회원 조회
        MemberEntity member = memberRepository.findById(uuid)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        // 가입 대기 상태인지 확인
        if (member.getStatus() != MemberStatus.PENDING) {
            throw new RuntimeException("가입 신청 상태에서만 지원을 취소할 수 있습니다.");
        }

        // 회원 삭제
        memberRepository.delete(member);
    }

    /**
     * 현재 로그인한 회원의 정보 조회
     */
    @Transactional(readOnly = true)
    public MemberResponse getMyInfo(HttpSession session) {
        Integer userId = getCurrentUserId(session);
        return getMemberInfo(userId);
    }

    /**
     * 현재 로그인한 회원의 정보 수정
     */
    @Transactional
    public MemberResponse updateMyInfo(HttpSession session, SignupRequest request) {
        Integer userId = getCurrentUserId(session);
        return updateApplication(userId, request);
    }

    /**
     * 현재 로그인한 회원의 가입 신청 취소
     */
    @Transactional
    public void deleteMyAccount(HttpSession session) {
        Integer userId = getCurrentUserId(session);
        cancelApplication(userId);
        session.invalidate();
    }

    /**
     * 현재 로그인한 회원의 영어 실력 레벨 변경 신청
     */
    @Transactional
    public void updateEnglishLevel(HttpSession session, Integer level) {
        Integer userId = getCurrentUserId(session);

        MemberEntity member = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        member.setLevel(level);
        memberRepository.save(member);
    }

    /**
     * 현재 로그인한 회원의 상태 변경 신청
     */
    @Transactional
    public void updateStatus(HttpSession session, MemberStatus status) {
        Integer userId = getCurrentUserId(session);

        MemberEntity member = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        member.setStatus(status);
        memberRepository.save(member);
    }

    /**
     * 세션에서 현재 로그인한 사용자 ID 가져오기
     */
    private Integer getCurrentUserId(HttpSession session) {
        Integer userId = (Integer) session.getAttribute(SESSION_USER_KEY);
        if (userId == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }
        return userId;
    }
}