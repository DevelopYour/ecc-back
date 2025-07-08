package com.seoultech.ecc.admin.controller;

import com.seoultech.ecc.admin.dto.AddTopicDto;
import com.seoultech.ecc.admin.dto.EditCategoryDto;
import com.seoultech.ecc.admin.dto.TopicDetailDto;
import com.seoultech.ecc.admin.service.AdminCategoryService;
import com.seoultech.ecc.admin.service.AdminTopicService;
import com.seoultech.ecc.member.dto.ResponseDto;
import com.seoultech.ecc.admin.dto.TopicCategoryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/admin/content/")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "스터디 콘텐츠 관리 API", description = "관리자 전용 콘텐츠 관리 API")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminContentController {

    private final AdminCategoryService adminCategoryService;
    private final AdminTopicService adminTopicService;

    @GetMapping("categories")
    @Operation(summary = "전체 주제 카테고리 조회", description = "주제 카테고리 전체 목록을 조회합니다.")
    public ResponseEntity<ResponseDto<List<TopicCategoryDto>>> getAllCategories() {
        List<TopicCategoryDto> categories = adminCategoryService.getAllCategories();
        return ResponseEntity.ok(ResponseDto.success(categories));
    }

    @PostMapping("categories")
    @Operation(summary = "카테고리 추가", description = "신규 주제 카테고리를 생성합니다.")
    public ResponseEntity<ResponseDto<TopicCategoryDto>> addCategory(@RequestBody EditCategoryDto dto){
        return ResponseEntity.ok(ResponseDto.success(adminCategoryService.add(dto)));
    }

    @PutMapping("categories/{categoryId}")
    @Operation(summary = "카테고리 수정", description = "특정 주제 카테고리를 수정합니다.")
    public ResponseEntity<ResponseDto<TopicCategoryDto>> updateCategory(@PathVariable Integer categoryId, @RequestBody EditCategoryDto dto){
        return ResponseEntity.ok(ResponseDto.success(adminCategoryService.update(categoryId, dto)));
    }

    @DeleteMapping("categories/{categoryId}")
    @Operation(summary = "카테고리 삭제", description = "특정 카테고리와 카테고리 내 주제 전체를 삭제합니다.")
    public ResponseEntity<ResponseDto<Void>> deleteCategory(@PathVariable Integer categoryId){
        adminCategoryService.delete(categoryId);
        return ResponseEntity.ok(ResponseDto.success(null));
    }

    @GetMapping("topics")
    @Operation(summary = "전체 주제 조회", description = "전체 주제 목록을 조회합니다.")
    public ResponseEntity<ResponseDto<List<TopicDetailDto>>> getAllTopics() {
        List<TopicDetailDto> categories = adminTopicService.getAllTopics();
        return ResponseEntity.ok(ResponseDto.success(categories));
    }

    @PostMapping("topics")
    @Operation(summary = "주제 추가", description = "신규 주제를 생성합니다.")
    public ResponseEntity<ResponseDto<TopicDetailDto>> addTopic(@RequestBody AddTopicDto dto){
        return ResponseEntity.ok(ResponseDto.success(adminTopicService.add(dto)));
    }

    @PutMapping("topics/{topicId}")
    @Operation(summary = "주제 수정", description = "특정 주제를 수정합니다.")
    public ResponseEntity<ResponseDto<TopicDetailDto>> updateTopic(@PathVariable Integer topicId, @RequestBody String topic){
        return ResponseEntity.ok(ResponseDto.success(adminTopicService.update(topicId, topic)));
    }

    @DeleteMapping("topics/{topicId}")
    @Operation(summary = "주제 삭제", description = "특정 주제를 삭제합니다.")
    public ResponseEntity<ResponseDto<Void>> deleteTopic(@PathVariable Integer topicId){
        adminTopicService.delete(topicId);
        return ResponseEntity.ok(ResponseDto.success(null));
    }
}
