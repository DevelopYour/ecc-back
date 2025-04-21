package com.seoultech.ecc.team.service;

import com.seoultech.ecc.member.datamodel.MemberEntity;
import com.seoultech.ecc.member.datamodel.MemberStatus;
import com.seoultech.ecc.member.repository.MemberRepository;
import com.seoultech.ecc.team.datamodel.ApplyRegularStudyEntity;
import com.seoultech.ecc.team.datamodel.SubjectEntity;
import com.seoultech.ecc.team.datamodel.TimeEntity;
import com.seoultech.ecc.team.dto.RegularStudyDto;
import com.seoultech.ecc.team.repository.ApplyRegularStudyRepository;
import com.seoultech.ecc.team.repository.SubjectRepository;
import com.seoultech.ecc.team.repository.TimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RegularStudyService {

    private final MemberRepository memberRepository;
    private final SubjectRepository subjectRepository;
    private final TimeRepository timeRepository;
    private final ApplyRegularStudyRepository applyRegularStudyRepository;

    /**
     * 정규 스터디 신청
     */
    @Transactional
    public RegularStudyDto.ApplyListResponse applyRegularStudy(String studentId, RegularStudyDto.ApplyRequest request) {
        // 회원 조회 및 상태 확인
        MemberEntity member = getMemberAndCheckStatus(studentId);

        List<ApplyRegularStudyEntity> savedEntities = new ArrayList<>();

        // 모든 과목과 시간 조합에 대해 신청 처리
        for (Long subjectId : request.getSubjectIds()) {
            SubjectEntity subject = getSubjectById(subjectId);

            for (Integer timeId : request.getTimeIds()) {
                TimeEntity time = getTimeById(timeId);

                // 신청 엔티티 생성 및 저장
                ApplyRegularStudyEntity entity = ApplyRegularStudyEntity.builder()
                        .member(member)
                        .subject(subject)
                        .time(time)
                        .build();

                savedEntities.add(applyRegularStudyRepository.save(entity));
            }
        }

        return RegularStudyDto.ApplyListResponse.fromEntityList(savedEntities);
    }

    /**
     * 정규 스터디 신청 내역 조회
     */
    @Transactional(readOnly = true)
    public RegularStudyDto.ApplyListResponse getRegularStudyApplications(String studentId) {
        // 회원 조회 및 상태 확인
        MemberEntity member = getMemberAndCheckStatus(studentId);

        // 신청 내역 조회
        List<ApplyRegularStudyEntity> applications = applyRegularStudyRepository.findByMember(member);

        return RegularStudyDto.ApplyListResponse.fromEntityList(applications);
    }

    /**
     * 정규 스터디 신청 내역 수정
     */
    @Transactional
    public RegularStudyDto.ApplyListResponse updateRegularStudyApplications(String studentId, RegularStudyDto.UpdateRequest request) {
        // 회원 조회 및 상태 확인
        MemberEntity member = getMemberAndCheckStatus(studentId);

        // 기존 신청 내역 모두 삭제
        applyRegularStudyRepository.deleteAllByMemberUuid(member.getUuid());

        // 새로운 신청 내역 등록
        List<ApplyRegularStudyEntity> savedEntities = new ArrayList<>();

        for (Long subjectId : request.getSubjectIds()) {
            SubjectEntity subject = getSubjectById(subjectId);

            for (Integer timeId : request.getTimeIds()) {
                TimeEntity time = getTimeById(timeId);

                ApplyRegularStudyEntity entity = ApplyRegularStudyEntity.builder()
                        .member(member)
                        .subject(subject)
                        .time(time)
                        .build();

                savedEntities.add(applyRegularStudyRepository.save(entity));
            }
        }

        return RegularStudyDto.ApplyListResponse.fromEntityList(savedEntities);
    }

    /**
     * 정규 스터디 신청 내역 취소 (전체)
     */
    @Transactional
    public void cancelRegularStudyApplications(String studentId) {
        // 회원 조회 및 상태 확인
        MemberEntity member = getMemberAndCheckStatus(studentId);

        // 모든 신청 내역 삭제
        applyRegularStudyRepository.deleteAllByMemberUuid(member.getUuid());
    }

    /**
     * 회원 조회 및 상태 확인 (ACTIVE만 가능)
     */
    private MemberEntity getMemberAndCheckStatus(String studentId) {
        MemberEntity member = memberRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다. 학번: " + studentId));

        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new RuntimeException("ACTIVE 상태의 회원만 정규 스터디를 신청할 수 있습니다.");
        }

        return member;
    }

    /**
     * 과목 ID로 과목 조회
     */
    private SubjectEntity getSubjectById(Long subjectId) {
        return subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 과목입니다. ID: " + subjectId));
    }

    /**
     * 시간 ID로 시간 조회
     */
    private TimeEntity getTimeById(Integer timeId) {
        return timeRepository.findById(timeId.longValue())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 시간입니다. ID: " + timeId));
    }
}