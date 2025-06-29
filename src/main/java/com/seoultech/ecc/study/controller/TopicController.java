package com.seoultech.ecc.study.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
@Tag(name = "스터디 주제 API", description = "스터디 주제 관련 API")
public class TopicController {
}
