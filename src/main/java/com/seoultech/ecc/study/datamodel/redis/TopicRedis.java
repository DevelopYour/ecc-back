package com.seoultech.ecc.study.datamodel.redis;
import com.seoultech.ecc.study.datamodel.TopicCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopicRedis {
    private Long topicId;
    private TopicCategory category;
    private String topic;
    private List<ExpressionRedis> expressions;
}
