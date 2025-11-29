package com.campus.campus.domain.school.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "학생증 OCR 결과 (학적 정보)")
public record StudentCardInfoDto(

        @Schema(description = "학생 이름", example = "최서연")
        String name,

        @Schema(description = "학년", example = "4")
        String grade,

        @Schema(description = "학번", example = "2021111111")
        String studentId,

        @Schema(description = "학과 또는 학부명", example = "정치국제학과")
        String department,

        @Schema(description = "OCR 전체 텍스트 리스트", example = "[\"서울여자대학교\", \"정치국제학과\", \"2021111111\", \"최서연\", \"4학년\"]")
        List<String> rawTexts
) {}
