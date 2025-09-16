package com.seoultech.ecc.team.service;

import com.seoultech.ecc.team.datamodel.SubjectEntity;
import com.seoultech.ecc.team.dto.SubjectDto;
import com.seoultech.ecc.team.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final TeamService teamService;

    // 과목 ID로 과목 조회
    public SubjectEntity getSubjectById(Integer subjectId) {
        return subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 과목입니다. ID: " + subjectId));
    }

    public Map<Integer, SubjectEntity> getSubjectMap(){
        return subjectRepository.findAll().stream()
                .collect(Collectors.toMap(SubjectEntity::getId, t -> t));
    }

    public List<SubjectDto> getAllSubjects() {
        return subjectRepository.findAll().stream().map(SubjectDto::fromEntity).collect(Collectors.toList());
    }

    // teamId로 해당 팀의 과목이 회화과목인지 일반(시험)과목인지 판단
    public Boolean isGeneralTeam(Integer teanId) {
        Integer subjectId = teamService.getSubjectId(teanId);
        return subjectId > 1; // 0(자유회화), 1(오픽)
    }
}
