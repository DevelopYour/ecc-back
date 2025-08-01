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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StudyService {

    @Autowired
    private ReportService reportService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private OpenAiService openAiService;

    @Autowired
    private StudyRepository studyRepository;

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
                studyDto.setStudyStatus(StudyStatus.COMPLETE);
                dto.setReviewSummaries(reviewService.getReviewStatusInfos(report.getId()));
            } else { // 보고서 미제출
                studyDto.setStudyStatus(StudyStatus.DRAFTING);
                dto.setReviewSummaries(null);
            }
            dto.setStudySummary(studyDto);
            dtos.add(dto);
        }
        return dtos;
    }

//    @Transactional
//    public StudyRedis getStudyRoom(Integer teamId) {
//        // teamId로 이미 진행 중인 study Redis 확인 후 있으면 반환
//        String existingStudyId = studyRepository.findStudyIdByTeamId(teamId); // 해당 팀의 스터디 redis 존재 여부 확인
//        if (existingStudyId != null) {
//            StudyRedis existingStudy = studyRepository.findByStudyId(existingStudyId); // 스터디Id로 redis 내용 확인
//            if (existingStudy != null) return existingStudy;
//            // 예외 처리 (키는 있는데 값은 없는 경우
//            throw new IllegalStateException("Study key exists but StudyRedis is null. (studyId=" + existingStudyId + ")");
//        }
//        // 없으면 생성
//        String reportId = reportService.createReport(teamId); // 1. 보고서 초안 생성 TODO: 보고서 초안은 있지만 study redis가 없는 경우 처리 필요 (생성날짜로 판단?)
//        StudyRedis studyRedis = new StudyRedis(reportId, teamId, new ArrayList<>()); // 2. Redis Study 객체 생성 (빈 topic 목록)
//        studyRepository.save(studyRedis); // 3. Redis 저장
//        studyRepository.saveTeamIndex(teamId, reportId);// 인덱싱용 저장
//        return studyRedis;
//    }

    @Transactional
    public StudyRedis getStudyRoom(Integer teamId) {
        System.out.println("=== getStudyRoom 시작 - teamId: " + teamId);

        // teamId로 이미 진행 중인 study Redis 확인 후 있으면 반환
        System.out.println("1. Redis에서 기존 studyId 조회 중...");
        String existingStudyId = studyRepository.findStudyIdByTeamId(teamId);
        System.out.println("1-1. 조회 결과 existingStudyId: " + existingStudyId);

        if (existingStudyId != null) {
            System.out.println("2. 기존 studyId 발견, StudyRedis 조회 중...");
            StudyRedis existingStudy = studyRepository.findByStudyId(existingStudyId);
            System.out.println("2-1. StudyRedis 조회 결과: " + (existingStudy != null ? "존재" : "null"));
            if (existingStudy != null) {
                System.out.println("2-2. 기존 StudyRedis 반환");
                return existingStudy;
            }
            // 예외 처리
            System.out.println("2-3. 키는 있는데 값이 null - 예외 발생 예정");
            throw new IllegalStateException("Study key exists but StudyRedis is null. (studyId=" + existingStudyId + ")");
        }

        System.out.println("3. 기존 study 없음, 새로 생성 시작");

        try {
            System.out.println("4. 보고서 초안 생성 중...");
            String reportId = reportService.createReport(teamId);
            System.out.println("4-1. 생성된 reportId: " + reportId);

            System.out.println("5. StudyRedis 객체 생성 중...");
            StudyRedis studyRedis = new StudyRedis(reportId, teamId, new ArrayList<>());
            System.out.println("5-1. StudyRedis 객체 생성 완료: " + studyRedis.getId());

            System.out.println("6. Redis 저장 중...");
            studyRepository.save(studyRedis);
            System.out.println("6-1. Redis 저장 완료");

            System.out.println("7. 팀 인덱싱 저장 중...");
            studyRepository.saveTeamIndex(teamId, reportId);
            System.out.println("7-1. 팀 인덱싱 저장 완료");

            System.out.println("=== getStudyRoom 완료 - 반환할 studyRedis ID: " + studyRedis.getId());
            return studyRedis;

        } catch (Exception e) {
            System.out.println("!!! 예외 발생: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

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
