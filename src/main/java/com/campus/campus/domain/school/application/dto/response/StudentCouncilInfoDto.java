package com.campus.campus.domain.school.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "당선 정보 OCR 결과")
public record StudentCouncilInfoDto(

        @Schema(description = "학생회 단위", example = "총학생회")
        String unit,

        @Schema(description = "당선인 이름", example = "최서연")
        String winnerName,

        @Schema(description = "학번", example = "2021111111")
        String studentId,

        @Schema(description = "학년", example = "4")
        String grade,

        @Schema(description = "학과 또는 학부명", example = "정치국제학과")
        String department,

        @Schema(description = "직책", example = "총학생회장")
        String position,

        @Schema(description = "OCR 전체 텍스트", example = "서울여자대학교 제00대 총학생회장 당선인 최서연 ...")
        String rawText
) {}
