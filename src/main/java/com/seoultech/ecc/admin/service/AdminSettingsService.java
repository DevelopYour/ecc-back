package com.seoultech.ecc.admin.service;

import com.seoultech.ecc.admin.datamodel.SemesterEntity;
import com.seoultech.ecc.admin.datamodel.SettingEntity;
import com.seoultech.ecc.admin.datamodel.SettingKey;
import com.seoultech.ecc.admin.dto.CreateSemesterDto;
import com.seoultech.ecc.admin.dto.SemesterDto;
import com.seoultech.ecc.admin.dto.SettingDto;
import com.seoultech.ecc.admin.repository.SemesterRepository;
import com.seoultech.ecc.admin.repository.SettingRepository;
import com.seoultech.ecc.team.service.ApplyStudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminSettingsService {

    private final SemesterRepository semesterRepository;
    private final SettingRepository settingRepository;
    private final ApplyStudyService applyStudyService;

    // 현재 설정 정보 조회
    public SettingDto getSettingInfo() {
        SettingDto dto = new SettingDto();
        dto.setCurrentSemester(getCurrentSemester());
        dto.setIsRecruiting(applyStudyService.getRecruitmentStatus());
        dto.setSemesters(semesterRepository.findAll().stream().map(SemesterDto::fromEntity).collect(Collectors.toList()));
        return dto;
    }

    // 현재 학기 조회
    public SemesterDto getCurrentSemester() {
        String semesterId = settingRepository.findBySettingKey(SettingKey.CURRENT_SEMESTER_ID.getKey()).getSettingValue();
        SemesterEntity semester = semesterRepository.findById(Integer.valueOf(semesterId)).get();
        return SemesterDto.fromEntity(semester);
    }

    // 현재 학기 아이디 조회
    public SemesterEntity getCurrentSemesterEntity() {
        String semesterId = settingRepository.findBySettingKey(SettingKey.CURRENT_SEMESTER_ID.getKey()).getSettingValue();
        return semesterRepository.findById(Integer.valueOf(semesterId)).get();
    }

    // 현재 학기 갱신
    @Transactional
    public void updateCurrentSemester(CreateSemesterDto dto) {
        // 새로운 Semester 추가
        SemesterEntity newSemester = new SemesterEntity();
        newSemester.setYear(dto.getYear());
        newSemester.setSemester(dto.getSemester());
        newSemester = semesterRepository.save(newSemester);

        // 현재 학기 아이디 수정
        SettingEntity currentSemester = settingRepository.findBySettingKey(SettingKey.CURRENT_SEMESTER_ID.getKey());
        currentSemester.setSettingValue(newSemester.getId().toString());
        settingRepository.save(currentSemester);
    }

    // 정규스터디 모집 여부 수정
    public void setRecruitmentStatus(Boolean recruitmentStatus) {
        SettingEntity entity = settingRepository.findBySettingKey(SettingKey.RECRUITMENT_STATUS.getKey());
        entity.setSettingValue(recruitmentStatus.toString());
        settingRepository.save(entity);
    }

}
