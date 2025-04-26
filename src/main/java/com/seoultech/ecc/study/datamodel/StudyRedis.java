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
    private Long teamId;
    private List<TopicRedis> topics;
}

