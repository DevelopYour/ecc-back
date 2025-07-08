package com.seoultech.ecc.team.service;

import com.seoultech.ecc.team.datamodel.TimeEntity;
import com.seoultech.ecc.team.repository.TimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TimeService {

    private final TimeRepository timeRepository;

    // 시간 ID로 시간 조회
    public TimeEntity getTimeById(Integer timeId) {
        return timeRepository.findById(timeId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 시간입니다. ID: " + timeId));
    }
}
