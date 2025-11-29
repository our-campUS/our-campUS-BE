package com.campus.campus.domain.school.presentation;

import com.campus.campus.domain.school.application.dto.response.StudentCardInfoDto;
import com.campus.campus.domain.school.application.dto.response.StudentCouncilInfoDto;
import com.campus.campus.domain.school.application.service.ClovaOcrService;
import com.campus.campus.global.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/school/ocr")
@Tag(name = "School OCR", description = "학생증·당선 정보 OCR API")
public class SchoolController {


    private final ClovaOcrService clovaOcrService;

    @Operation(summary = "학생증 OCR", description = "학생증 이미지를 업로드하여 OCR 수행")
    @PostMapping(value = "/student-card", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResponse<StudentCardInfoDto> studentCard(
            @Parameter(description = "학생증 이미지 파일", required = true)
            @RequestPart MultipartFile file
    ) {
        StudentCardInfoDto result = clovaOcrService.processStudentCard(file);
        return CommonResponse.success(SchoolResponseCode.OCR_STUDENT_CARD_SUCCESS, result);
    }

    @Operation(summary = "당선 정보 OCR", description = "학생회 당선증 이미지를 업로드하여 OCR 수행")
    @PostMapping(value = "/student-council", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResponse<StudentCouncilInfoDto> studentCouncil(
            @Parameter(description = "학생회 당선증 이미지 파일", required = true)
            @RequestPart MultipartFile file
    ) {
        StudentCouncilInfoDto result = clovaOcrService.processStudentCouncil(file);
        return CommonResponse.success(SchoolResponseCode.OCR_STUDENT_COUNCIL_SUCCESS, result);
    }
}
