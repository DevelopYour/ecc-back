package com.seoultech.ecc.study.repository;

import com.seoultech.ecc.study.datamodel.StudyRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
public class StudyRepository{

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final Duration TTL = Duration.ofHours(2);

    private String studyKey(String studyId) {
        return "study:" + studyId;
    }

    private String teamIndexKey(Integer teamId) {
        return "team:" + teamId + ":study";
    }

    public StudyRedis findByStudyId(String studyId) {
        return (StudyRedis) redisTemplate.opsForValue().get(studyKey(studyId));
    }

    public StudyRedis save(StudyRedis studyRedis) {
        redisTemplate.opsForValue().set(studyKey(studyRedis.getId()), studyRedis, TTL);
        return studyRedis;
    }

    public void deleteByStudyId(String studyId) {
        redisTemplate.delete(studyKey(studyId));
    }

    public void saveTeamIndex(Integer teamId, String studyId) {
        redisTemplate.opsForValue().set(teamIndexKey(teamId), studyId, TTL);
    }

    public String findStudyIdByTeamId(Integer teamId) {
        return (String) redisTemplate.opsForValue().get(teamIndexKey(teamId));
    }

    public void deleteTeamIndex(Integer teamId) {
        redisTemplate.delete(teamIndexKey(teamId));
    }
}

