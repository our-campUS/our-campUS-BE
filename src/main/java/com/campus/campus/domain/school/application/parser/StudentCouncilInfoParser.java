package com.campus.campus.domain.school.application.parser;

import com.campus.campus.domain.school.application.dto.response.ClovaOcrResponseDto;
import com.campus.campus.domain.school.application.dto.response.StudentCouncilInfoDto;
import java.util.List;

public class StudentCouncilInfoParser {

    public static StudentCouncilInfoDto parse(ClovaOcrResponseDto response) {

        List<String> texts = extractAllTexts(response);

        return new StudentCouncilInfoDto(
                extractName(texts),
                extractPosition(texts),
                extractDepartment(texts),
                texts
        );
    }

    private static List<String> extractAllTexts(ClovaOcrResponseDto response) {
        return response.images().get(0).fields()
                .stream()
                .map(ClovaOcrResponseDto.Field::inferText)
                .toList();
    }

    private static String extractName(List<String> texts) {
        return texts.stream()
                .filter(t -> t.matches("^[가-힣]{2,4}$"))
                .findFirst()
                .orElse(null);
    }

    private static String extractPosition(List<String> texts) {
        List<String> keywords = List.of("회장", "부회장", "국장", "차장", "위원");

        return texts.stream()
                .filter(t -> keywords.stream().anyMatch(t::contains))
                .findFirst()
                .orElse(null);
    }

    private static String extractDepartment(List<String> texts) {
        return texts.stream()
                .filter(t -> t.contains("학과") || t.contains("학부"))
                .findFirst()
                .orElse(null);
    }
}
