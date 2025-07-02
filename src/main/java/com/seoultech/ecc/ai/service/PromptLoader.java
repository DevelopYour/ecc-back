package com.seoultech.ecc.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seoultech.ecc.review.dto.ReviewQuestionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class PromptLoader {

    private final ConcurrentHashMap<String, String> promptCache = new ConcurrentHashMap<>();

    public String createTranslationPrompt(String question, boolean korean) {
        String promptTemplate = loadPrompt(korean ? "translation-to-english.txt" : "translation-to-korean.txt");
        return formatPrompt(promptTemplate, question);
    }

    public String createFeedbackPrompt(String question) {
        return formatPrompt(loadPrompt("feedback.txt"), question);
    }

    public String createGradePrompt(List<ReviewQuestionDto> questions) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String questionsJson = objectMapper.writeValueAsString(questions);
            return formatPrompt(loadPrompt("grade.txt"), questionsJson);
        } catch (Exception e) {
            log.error("Failed to serialize questions to JSON", e);
            throw new RuntimeException("Failed to create grade prompt", e);
        }
    }

    private String loadPrompt(String promptFileName) {
        return promptCache.computeIfAbsent(promptFileName, this::readPromptFromFile);
    }

    private String readPromptFromFile(String fileName) {
        try {
            ClassPathResource resource = new ClassPathResource("prompts/" + fileName);
            return resource.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Failed to load prompt file: {}", fileName, e);
            throw new RuntimeException("Failed to load prompt template: " + fileName, e);
        }
    }

    private String formatPrompt(String promptTemplate, Object... args) {
        return String.format(promptTemplate, args);
    }
}
