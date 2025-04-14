package com.seoultech.ecc.member.controller;

import com.seoultech.ecc.member.dto.MajorDto;
import com.seoultech.ecc.member.repository.MajorRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/major")
@RequiredArgsConstructor
public class MajorController {

    @Autowired
    private MajorRepository majorRepository;

    @GetMapping
    @Operation(summary = "학과 목록 조회", description = "학과 목록을 조회합니다.")
    public ResponseEntity<List<MajorDto>> findAllMajor() {
        return ResponseEntity.ok(majorRepository.findAll().stream().map(MajorDto::fromEntity).toList());
    }
}
