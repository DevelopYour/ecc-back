package com.seoultech.ecc.team.service;

import com.seoultech.ecc.member.datamodel.MemberEntity;
import com.seoultech.ecc.member.datamodel.MemberStatus;
import com.seoultech.ecc.member.repository.MemberRepository;
import com.seoultech.ecc.team.datamodel.*;
import com.seoultech.ecc.team.dto.ApplyStudyDto;
import com.seoultech.ecc.team.repository.ApplyRegularSubjectRepository;
import com.seoultech.ecc.team.repository.ApplyRegularTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplyStudyService {

    private final MemberRepository memberRepository;
    private final SubjectService subjectService;
    private final TimeService timeService;
    private final ApplyRegularTimeRepository applyTimeRepository;
    private final ApplyRegularSubjectRepository applySubjectRepository;

    // 정규 스터디 신청 (UUID 사용)
    @Transactional
    public ApplyStudyDto.ApplyResponse applyRegularStudy(Integer uuid, ApplyStudyDto.ApplyRequest request) {
        // 회원 조회 및 상태 확인
        MemberEntity member = getMemberAndCheckStatus(uuid);

        // 새 신청 내역 저장
        List<ApplyRegularSubjectEntity> subjects = request.getSubjectIds().stream()
                .map(subjectId -> ApplyRegularSubjectEntity.builder()
                        .member(member)
                        .subject(subjectService.getSubjectById(subjectId))
                        .build())
                .collect(Collectors.toList());

        List<ApplyRegularTimeEntity> times = request.getTimeIds().stream()
                .map(timeId -> ApplyRegularTimeEntity.builder()
                        .member(member)
                        .time(timeService.getTimeById(timeId))
                        .build())
                .collect(Collectors.toList());

        List<ApplyRegularSubjectEntity> subjectEntities = applySubjectRepository.saveAll(subjects);
        List<ApplyRegularTimeEntity> timeEntites = applyTimeRepository.saveAll(times);

        return toApplyResponse(member, subjectEntities, timeEntites);
    }

    // 정규 스터디 수정 (UUID 사용)
    @Transactional
    public ApplyStudyDto.ApplyResponse updateRegularStudy(Integer uuid, ApplyStudyDto.ApplyRequest request) {
        // 기존 신청 내역 삭제
        deleteStudyApplications(uuid);
        // 새 신청 내역 저장
        return applyRegularStudy(uuid, request);
    }


    // 정규 스터디 신청 내역 조회 (UUID 사용)
    @Transactional(readOnly = true)
    public ApplyStudyDto.ApplyResponse getRegularStudyApplications(Integer uuid) {
        // 회원 조회 및 상태 확인
        MemberEntity member = getMemberAndCheckStatus(uuid);

        // 신청 내역 조회
        List<ApplyRegularSubjectEntity> subjects = applySubjectRepository.findByMember(member);
        List<ApplyRegularTimeEntity> times = applyTimeRepository.findByMember(member);

        return toApplyResponse(member, subjects, times);
    }

    // 정규 스터디 신청 내역 취소 (전체) (UUID 사용)
    @Transactional
    public void deleteStudyApplications(Integer uuid) {
        // 회원 조회 및 상태 확인
        MemberEntity member = getMemberAndCheckStatus(uuid);

        // 모든 신청 내역 삭제
        applyTimeRepository.deleteByMember(member);
        applySubjectRepository.deleteByMember(member);
    }

    // 회원 조회 및 상태 확인 (ACTIVE만 가능) (UUID 사용)
    private MemberEntity getMemberAndCheckStatus(Integer uuid) {
        MemberEntity member = memberRepository.findById(uuid)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다. UUID: " + uuid));

        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new RuntimeException("ACTIVE 상태의 회원만 정규 스터디를 신청할 수 있습니다.");
        }

        return member;
    }

    private ApplyStudyDto.ApplyResponse toApplyResponse(MemberEntity member, List<ApplyRegularSubjectEntity> subjects, List<ApplyRegularTimeEntity> times){
        return ApplyStudyDto.ApplyResponse.builder()
                .memberUuid(member.getUuid())
                .memberName(member.getName())
                .subjects(subjects.stream().map(ApplyStudyDto.ApplyResponse.ApplySubjectDto::fromEntity).collect(Collectors.toList()))
                .times(times.stream().map(ApplyStudyDto.ApplyResponse.ApplyTimeDto::fromEntity).collect(Collectors.toList()))
                .build();
    }
}