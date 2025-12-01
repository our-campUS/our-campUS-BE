package com.campus.campus.domain.school.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "학생회 당선 OCR 결과")
public record StudentCouncilInfoDto(

        @Schema(description = "학생 이름", example = "최서연")
        String name,

        @Schema(description = "직책", example = "학생회장")
        String position,

        @Schema(description = "학과 또는 학부명", example = "정치국제학과")
        String department,

        @Schema(description = "OCR 전체 텍스트 리스트")
        List<String> rawTexts
) {}