package com.seoultech.ecc.study.controller;

import com.seoultech.ecc.ai.OpenAiService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AiController {
    private final OpenAiService openAiService;

    public AiController(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @GetMapping
    public String ask(@RequestParam String input) {
        return openAiService.chat(input);
    }
}
