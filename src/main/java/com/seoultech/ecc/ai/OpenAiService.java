package com.seoultech.ecc.ai;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class OpenAiService {

    private final WebClient openAiWebClient;

    public OpenAiService(WebClient openAiWebClient) {
        this.openAiWebClient = openAiWebClient;
    }

    public String chat(String userInput) {
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", List.of(Map.of("role", "user", "content", userInput))
        );

        MyChatCompletion response = openAiWebClient.post()
                .uri("/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(MyChatCompletion.class)
                .block();

        return response.choices.get(0).message.content;
    }
}

