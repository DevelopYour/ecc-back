package com.seoultech.ecc.study.datamodel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudyRedis {
    private String id;
    private Integer teamId;
    private List<TopicRedis> topics; // 회화 과목 (자유회화, 오픽)
    private GeneralRedis general; // 일반 시험 과목 (토익, 토플, 아이엘츠)
}

