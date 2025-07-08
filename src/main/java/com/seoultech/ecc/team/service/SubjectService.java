package com.seoultech.ecc.team.service;

import com.seoultech.ecc.team.datamodel.SubjectEntity;
import com.seoultech.ecc.team.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;

    // 과목 ID로 과목 조회
    public SubjectEntity getSubjectById(Integer subjectId) {
        return subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 과목입니다. ID: " + subjectId));
    }
}
