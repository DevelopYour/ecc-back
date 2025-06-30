package com.seoultech.ecc.ai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class PromptLoader {

    private final ConcurrentHashMap<String, String> promptCache = new ConcurrentHashMap<>();

    public String loadPrompt(String promptFileName) {
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

    public String formatPrompt(String promptTemplate, Object... args) {
        return String.format(promptTemplate, args);
    }
}
