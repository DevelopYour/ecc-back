package com.seoultech.ecc.study.service;

import com.seoultech.ecc.ai.dto.AiExpressionResponse;
import com.seoultech.ecc.ai.service.OpenAiService;
import com.seoultech.ecc.report.datamodel.ReportDocument;
import com.seoultech.ecc.report.dto.ReportFeedbackDto;
import com.seoultech.ecc.report.dto.ReportTopicDto;
import com.seoultech.ecc.report.dto.ReportTranslationDto;
import com.seoultech.ecc.report.service.ReportService;
import com.seoultech.ecc.review.service.ReviewService;
import com.seoultech.ecc.study.datamodel.*;
import com.seoultech.ecc.study.repository.StudyRepository;
import com.seoultech.ecc.study.dto.*;
import com.seoultech.ecc.team.service.SubjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StudyService {

    @Autowired
    private ReportService reportService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private OpenAiService openAiService;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private SubjectService subjectService;

    public List<WeeklySummaryDto> getTeamProgress(Integer teamId) {
        List<ReportDocument> reports = reportService.findReportsByTeamId(teamId);
        List<WeeklySummaryDto> dtos = new ArrayList<>();
        for (ReportDocument report : reports) {
            WeeklySummaryDto dto = new WeeklySummaryDto();
            StudySummaryDto studyDto = new StudySummaryDto();
            studyDto.setTeamId(teamId);
            studyDto.setWeek(report.getWeek());
            if (studyRepository.findByStudyId(report.getId()) != null) { // 진행중
                studyDto.setStudyStatus(StudyStatus.ONGOING);
                dto.setReviewSummaries(null);
            } else if(report.isSubmitted()){ // 보고서 제출 완료
                studyDto.setReportId(report.getId());
                studyDto.setStudyStatus(StudyStatus.COMPLETE);
                dto.setReviewSummaries(reviewService.getReviewStatusInfos(report.getId()));
            } else { // 보고서 미제출
                studyDto.setReportId(report.getId());
                studyDto.setStudyStatus(StudyStatus.DRAFTING);
                dto.setReviewSummaries(null);
            }
            dto.setStudySummary(studyDto);
            dtos.add(dto);
        }
        return dtos;
    }

    @Transactional
    public StudyEnterDto getStudyRoom(Integer teamId) {
        StudyEnterDto studyEnterDto = new StudyEnterDto();
        studyEnterDto.setIsGeneral(subjectService.isGeneralTeam(teamId));

        String existingStudyId = studyRepository.findStudyIdByTeamId(teamId);
        // teamId로 이미 진행 중인 study Redis 확인 후 있으면 아이디 반환
        if (existingStudyId != null) {
            studyEnterDto.setStudyId(existingStudyId);
        }
        // 없으면 생성
        log.info("team " + teamId + "의 study redis 생성 시작");
        try {
            String reportId = reportService.createReport(teamId);
            StudyRedis studyRedis = new StudyRedis(reportId, teamId, new ArrayList<>(), null);
            studyRepository.save(studyRedis);
            studyRepository.saveTeamIndex(teamId, reportId);
            studyEnterDto.setStudyId(studyRedis.getId());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return studyEnterDto;
    }

    @Transactional
    public StudyRedis getStudyData(String studyId) {
        return studyRepository.findByStudyId(studyId);
    }

    @Transactional
    public StudyRedis addTopicToStudy(String studyId, List<TopicDto> topicDtos) {
        // 1. Redis에서 기존 StudyRedis 객체 불러오기
        StudyRedis study = studyRepository.findByStudyId(studyId);
        if (study == null) throw new IllegalArgumentException("Study not found for id: " + studyId);

        // 2. topic 리스트에 추가
        for (TopicDto dto : topicDtos) {
            TopicRedis topic = new TopicRedis();
            Integer newTopicId = study.getTopics().size() + 1;
            topic.setTopicId(newTopicId);
            topic.setTopic(dto.getTopic());
            topic.setCategory(dto.getCategory());
            topic.setExpressions(new ArrayList<>());
            study.getTopics().add(topic);
        }

        // 3. 다시 Redis에 저장 (전체 객체 갱신)
        studyRepository.save(study);
        return study;
    }

    @Transactional
    public StudyRedis getAiHelpAndAdd(String studyId, ExpressionToAskDto questionDto) {
        validateInput(studyId, questionDto); // 1. Input validation

        StudyRedis study = studyRepository.findByStudyId(studyId); // 2. Find study
        if (study == null) {
            throw new IllegalArgumentException("Study not found for id: " + studyId);
        }

        TopicRedis targetTopic = findTopicInStudy(study, questionDto.getTopicId()); // 3. Find topic

        // 4. Generate AI translation
        AiExpressionResponse aiResponse = questionDto.isTranslation() ? // 질문 유형(번역/피드백)에 따라 요청 처리
                openAiService.generateTranslation(questionDto.getQuestion(), questionDto.isKorean()) : openAiService.generateFeedback(questionDto.getQuestion());

        // 5. Create and add expression
        ExpressionRedis expression = createExpression(targetTopic, questionDto, aiResponse);
        targetTopic.getExpressions().add(expression);

        // 6. Save updated study
        studyRepository.save(study);

        System.out.printf("Successfully added AI-generated expression for study: %s, topic: %d",
                studyId, questionDto.getTopicId());

        return study;
    }

    private void validateInput(String studyId, ExpressionToAskDto questionDto) {
        if (studyId == null || studyId.trim().isEmpty()) {
            throw new IllegalArgumentException("Study ID cannot be null or empty");
        }
        if (questionDto == null) {
            throw new IllegalArgumentException("Question DTO cannot be null");
        }
        if (questionDto.getQuestion() == null || questionDto.getQuestion().trim().isEmpty()) {
            throw new IllegalArgumentException("Question cannot be null or empty");
        }
        if (questionDto.getTopicId() == null) {
            throw new IllegalArgumentException("Topic ID cannot be null");
        }
    }

    private TopicRedis findTopicInStudy(StudyRedis study, Integer topicId) {
        return study.getTopics().stream()
                .filter(t -> t.getTopicId().equals(topicId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Topic not found in study"));
    }

    private ExpressionRedis createExpression(TopicRedis topic, ExpressionToAskDto questionDto, AiExpressionResponse aiResponse) {
        ExpressionRedis expression = new ExpressionRedis();

        // Generate next sequential ID
        Integer newExpressionId = topic.getExpressions().stream()
                .mapToInt(ExpressionRedis::getExpressionId)
                .max()
                .orElse(0) + 1;

        expression.setExpressionId(newExpressionId);
        expression.setKorean(aiResponse.getKorean());
        expression.setEnglish(aiResponse.getEnglish());
        expression.setTranslation(questionDto.isTranslation());
        if (questionDto.isTranslation()) { // 번역
            expression.setExampleEnglish(aiResponse.getExampleEnglish());
            expression.setExampleKorean(aiResponse.getExampleKorean());
        } else { // 교정
            expression.setFeedback(aiResponse.getFeedback());
            expression.setOriginal(questionDto.getQuestion());
        }
        return expression;
    }

    @Transactional
    public String finishStudy(String studyId) {
        StudyRedis study = studyRepository.findByStudyId(studyId);
        if (study == null) throw new IllegalArgumentException("Study not found for id: " + studyId);
        ReportDocument report = reportService.findByReportId(studyId);

        // StudyRedis 데이터 ReportDocument로 옮기기
        report.setTopics(fromRedisToDocument(study));
        reportService.saveReport(report);

        // Redis 키 삭제 (study, team 인덱싱)
        studyRepository.deleteByStudyId(studyId);
        studyRepository.deleteTeamIndex(study.getTeamId());
        return studyId; // == reportId
    }

    @Transactional
    public String submitReportAndCreateReview(String reportId) {
        ReportDocument report = reportService.findByReportId(reportId);
        report.setSubmitted(true);
        report.setSubmittedAt(LocalDateTime.now());
        reviewService.createReviews(report);
        return reportService.saveReport(report);
    }

    public ReportDocument getReport(String reportId) {
        return reportService.findByReportId(reportId);
    }

    private List<ReportTopicDto> fromRedisToDocument(StudyRedis study) {
        return study.getTopics().stream()
                .map(topic -> {
                    Map<Boolean, List<ExpressionRedis>> grouped = topic.getExpressions()
                            .stream()
                            .collect(Collectors.partitioningBy(ExpressionRedis::isTranslation));

                    return ReportTopicDto.builder()
                            .category(topic.getCategory())
                            .topic(topic.getTopic())
                            .feedbacks(grouped.get(false).stream()
                                    .map(ReportFeedbackDto::fromRedis)
                                    .collect(Collectors.toList()))
                            .translations(grouped.get(true).stream()
                                    .map(ReportTranslationDto::fromRedis)
                                    .collect(Collectors.toList()))
                            .build();
                })
                .collect(Collectors.toList());
    }
}
