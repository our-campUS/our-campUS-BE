package com.campus.campus.domain.school.presentation;

import com.campus.campus.domain.school.application.dto.response.StudentCardInfoDto;
import com.campus.campus.domain.school.application.dto.response.StudentCouncilInfoDto;
import com.campus.campus.domain.school.application.service.SchoolOcrService;
import com.campus.campus.global.auth.annotation.CurrentUserId;
import com.campus.campus.global.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/school/ocr")
@Tag(name = "School OCR", description = "학생증 / 학생회 당선증 OCR API")
public class SchoolController {

    private final SchoolOcrService ocrService;

    // ------------------------------
    // 1) 학생증 OCR
    // ------------------------------
    @Operation(summary = "학생증 OCR", description = "이미 업로드된 이미지 기반으로 학생증 OCR을 수행합니다.")
    @PostMapping("/student-card")
    public CommonResponse<StudentCardInfoDto> studentCard(
            @CurrentUserId Long userId,
            @RequestParam Long imageId
    ) {
        StudentCardInfoDto result = ocrService.processStudentCard(userId, imageId);
        return CommonResponse.success(SchoolResponseCode.OCR_STUDENT_CARD_SUCCESS, result);
    }

    // ------------------------------
    // 2) 학생회 당선증 OCR
    // ------------------------------
    @Operation(summary = "학생회 당선증 OCR", description = "이미 업로드된 이미지 기반으로 학생회 당선증 OCR을 수행합니다.")
    @PostMapping("/student-council")
    public CommonResponse<StudentCouncilInfoDto> studentCouncil(
            @CurrentUserId Long userId,
            @RequestParam Long imageId
    ) {
        StudentCouncilInfoDto result = ocrService.processStudentCouncil(userId, imageId);
        return CommonResponse.success(SchoolResponseCode.OCR_STUDENT_COUNCIL_SUCCESS, result);
    }
}
