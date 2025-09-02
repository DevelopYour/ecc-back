package com.seoultech.ecc.admin.service;

import com.seoultech.ecc.admin.datamodel.SemesterEntity;
import com.seoultech.ecc.admin.datamodel.SettingEntity;
import com.seoultech.ecc.admin.datamodel.SettingKey;
import com.seoultech.ecc.admin.dto.SemesterDto;
import com.seoultech.ecc.admin.repository.SemesterRepository;
import com.seoultech.ecc.admin.repository.SettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminSettingsService {

    private final SemesterRepository semesterRepository;
    private final SettingRepository settingRepository;

    // 현재 학기 조회
    public SemesterDto getCurrentSemester() {
        String semesterId = settingRepository.findBySettingKey(SettingKey.CURRENT_SEMESTER_ID.getKey()).getSettingValue();
        SemesterEntity semester = semesterRepository.findById(Integer.valueOf(semesterId)).get();
        return SemesterDto.fromEntity(semester);
    }

    // 현재 학기 갱신
    @Transactional
    public void updateCurrentSemester(SemesterDto dto) {
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
    }

}
